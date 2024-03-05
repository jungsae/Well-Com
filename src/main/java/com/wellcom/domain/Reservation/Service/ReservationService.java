package com.wellcom.domain.Reservation.Service;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Repository.DeskRepository;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.domain.Reservation.Dto.ReservationCreateReqDto;
import com.wellcom.domain.Reservation.Dto.ReservationResDto;
import com.wellcom.domain.Reservation.Repository.ReservationRepository;
import com.wellcom.domain.Reservation.Reservation;
import com.wellcom.domain.Reservation.Status;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final DeskRepository deskRepository;
    private final ReservationScheduler reservationScheduler;
    public ReservationService(ReservationRepository reservationRepository, MemberRepository memberRepository, DeskRepository deskRepository, ReservationScheduler reservationScheduler) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.deskRepository = deskRepository;
        this.reservationScheduler = reservationScheduler;
    }
    private Member findMemberByEmail() { //맴버 뽑아내기 공통화
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }
    private Desk findDeskByDeskNum(int deskNum) { // 테이블 뽑아내기 공통화
        return deskRepository.findByDeskNum(deskNum)
                .orElseThrow(() -> new EntityNotFoundException("없는 테이블 번호입니다"));
    }
    private Reservation findByReservationIdAndEmail(String reservationId){ // 유저가 예약했던 예약찾기(조건 둘 다 만족해야함)
        Member member = this.findMemberByEmail();
        return reservationRepository.findByReservationIdAndMember(reservationId, member)
                .orElseThrow(() -> new EntityNotFoundException("예약 내역이 존재하지 않습니다."));
    }
    private boolean isReservationPossible(int deskNum, LocalDateTime startTime, int durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        //즉시사용중인 테이블의 시간까지 범위안으로 포함시켜야하기 때문에 LocalDateTime.now()에서 LocalDate.now().atStartOfDay()로 당일 00시부터 검색
        List<Reservation> existingReservations = reservationRepository.findActiveReservationsByDeskAndTime(deskNum, LocalDate.now().atStartOfDay());

        for (Reservation existingReservation : existingReservations) {
            LocalDateTime existingStart = existingReservation.getStartTime();
            LocalDateTime existingEnd = existingReservation.getEndTime();
            // 새 예약의 시작 시간이 기존 예약의 종료 시간과 정확히 일치하는 경우는 허용
            // ex).15시 - 15시30분 예약이 존재 할 때, 15시30분은 예약이 가능
            if (startTime.equals(existingEnd)) continue; // 다음 예약으로 넘어감
            // 새 예약의 시작 시간이나 종료 시간이 기존 예약 시간대와 겹치는 경우
            if (startTime.isBefore(existingEnd) && endTime.isAfter(existingStart)) return false; // 중복되는 시간대가 있으므로 예약 불가
        }
        return true; // 중복되는 시간대가 없으므로 예약 가능
    }
    private boolean isImmediateUsePossible(int deskNum, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> futureReservations = reservationRepository.findActiveReservationsByDeskAndTime(deskNum, now);

        if (futureReservations.isEmpty()) return true; // 미래에 예약된 내역이 없으면 즉시 사용 가능
        else {
            LocalDateTime nextReservationStart = futureReservations.get(0).getStartTime();
            return endTime.isBefore(nextReservationStart);
        }
    }
    @CachePut(cacheNames = "findAllDesks", key = "#root.methodName")
    public Reservation saveInstantUse(ReservationCreateReqDto reservationCreateReqDto) {
        Member member = this.findMemberByEmail();
        Desk desk = findDeskByDeskNum(reservationCreateReqDto.getDeskNum());
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(reservationCreateReqDto.getMinutes());

        if (!isImmediateUsePossible(desk.getDeskNum(), endTime))
            throw new IllegalArgumentException("선택한 시간에 즉시 사용이 불가능합니다.");
        if (desk.getIsUsable().toString().equals("N"))
            throw new IllegalArgumentException("이미 사용중인 테이블입니다.");

        Reservation reservation = Reservation.builder()
                .reservationId(ReservationNumberGenerator.generateReservationNumber())
                .member(member)
                .desk(desk)
                .status(Status.USING)
                .startTime(LocalDateTime.now().withSecond(0).withNano(0))
                .cntPeople(reservationCreateReqDto.getCntPeople())
                .reservationTime(reservationCreateReqDto.getMinutes())
                .endTime(endTime.withSecond(0).withNano(0))
                .build();

        desk.updateIsUsable("N");
        reservationScheduler.scheduleReservationEnd(reservation);
        return reservationRepository.save(reservation);
    }
    @CachePut(cacheNames = "findAllDesks", key = "#root.methodName")
    public Reservation saveReservation(ReservationCreateReqDto reservationCreateReqDto) {
        Member member = this.findMemberByEmail();
        Desk desk = this.findDeskByDeskNum(reservationCreateReqDto.getDeskNum());
        LocalDateTime currentTime = LocalDateTime.now();

        if (!isReservationPossible(desk.getDeskNum(), reservationCreateReqDto.getStartTime(), reservationCreateReqDto.getMinutes()))
            throw new IllegalArgumentException("선택한 시간에 예약이 불가능합니다.");
        if (desk.getSeats() < reservationCreateReqDto.getCntPeople())
            throw new IllegalArgumentException("테이블 사용 가능 인원을 초과했습니다.");
        if (reservationCreateReqDto.getStartTime().isBefore(currentTime.minusSeconds(1)))
            throw new IllegalArgumentException("잘못된 시간을 선택하셨습니다.");
//        if (reservationCreateReqDto.getStartTime().isBefore(currentTime.plusMinutes(5)))
//            throw new IllegalArgumentException("원하시는 이용 시작 시간으로부터 5분 이내에는 예약이 불가능합니다.");

        Reservation reservation = Reservation.builder()
                .reservationId(ReservationNumberGenerator.generateReservationNumber())
                .member(member)
                .desk(desk)
                .status(Status.WAITING)
                .startTime(reservationCreateReqDto.getStartTime())
                .cntPeople(reservationCreateReqDto.getCntPeople())
                .reservationTime(reservationCreateReqDto.getMinutes())
                .endTime(reservationCreateReqDto.getStartTime().plusMinutes(reservationCreateReqDto.getMinutes()))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        reservationScheduler.scheduleReservationStart(savedReservation);
        reservationScheduler.scheduleReservationEnd(savedReservation);
        return savedReservation;
    }
    @CacheEvict(cacheNames = "findAllDesks", key = "#root.methodName")
    public String cancelReservation(String reservationId) {
        Reservation reservation = this.findByReservationIdAndEmail(reservationId);

        if (reservation.getStartTime().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("이미 시작된 예약은 취소할 수 없습니다.");
        if (reservation.getStatus().equals(Status.USING))
            throw new IllegalArgumentException("이미 사용 중인 예약은 취소할 수 없습니다.");
        if (reservation.getStatus().equals(Status.COMPLETE))
            throw new IllegalArgumentException("이미 완료된 예약은 취소할 수 없습니다.");
        if (reservation.getStatus().equals(Status.CANCELED))
            throw new IllegalArgumentException("이미 취소된 예약입니다.");

        reservationScheduler.cancelFutureSchedule(reservationId);
        reservation.setStatus("CANCELED");
        return reservationRepository.save(reservation).getReservationId();
    }
    public List<ReservationResDto> getReservations(int deskNum, String date) {
        LocalDate localDate = LocalDate.parse(date); //조회를 원하는 날짜 ex). 2024-02-17
        LocalDateTime startOfDay = localDate.atStartOfDay(); //하루의 시작 시간부터
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX); //하루의 끝 시간까지
        List<Reservation> reservations = reservationRepository.findReservationsByDeskAndTime(deskNum, startOfDay, endOfDay);
        return reservations.stream()
                .map(reservation -> ReservationResDto.builder()
                        .deskNum(reservation.getDesk().getDeskNum())
                        .status(String.valueOf(reservation.getStatus()))
                        .startTime(reservation.getStartTime())
                        .endTime(reservation.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }
}
