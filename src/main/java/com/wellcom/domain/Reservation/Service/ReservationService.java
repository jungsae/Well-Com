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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Duration;
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
    private Member findMemberByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Member does not exist: " + email));
    } //맴버 뽑아내기 공통화
    private Desk findDeskByDeskNum(int deskNum) {
        return deskRepository.findByDeskNum(deskNum)
                .orElseThrow(() -> new EntityNotFoundException("Desk dose not exist: " + deskNum));
    } // 테이블 뽑아내기 공통화
    private Reservation findByReservationIdAndEmail(String reservationId){
        Member member = this.findMemberByEmail();
        return reservationRepository.findByReservationIdAndMember(reservationId, member);
    } // 유저가 예약했던 예약찾기(조건 둘 다 만족해야함)
    private boolean isReservationPossible(int deskNum, LocalDateTime startTime, int durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        List<Reservation> activeReservations = reservationRepository.findActiveReservationsByDeskAndTime(deskNum, LocalDateTime.now());
        for (Reservation reservation : activeReservations) {
            if (startTime.isBefore(reservation.getEndTime()) && endTime.isAfter(reservation.getStartTime()))
                return false;
        }
        return true; // 요청된 예약 시간이 기존의 어떤 예약과도 겹치지 않음
    }

// 중복 예약을 방지하기 위해, 기존 예약과 시간대가 겹치지 않아야 예약이 가능하다.
//    private boolean isReservationPossible(int deskNum, LocalDateTime startTime, int durationMinutes) {
//        LocalDateTime endTime = startTime.plusMinutes(durationMinutes); // 예약 종료 시간 계산
//        List<Reservation> existingReservations = reservationRepository.findActiveReservationsByDeskAndTime(deskNum, LocalDateTime.now()); // 현재 시간 이후의 활성 예약 조회
//
//        for (Reservation existingReservation : existingReservations) {
//            LocalDateTime existingStart = existingReservation.getStartTime(); // 기존 예약의 시작 시간
//            LocalDateTime existingEnd = existingReservation.getEndTime(); // 기존 예약의 종료 시간
//
//            // 시작 시간이나 종료 시간이 기존 예약 시간대와 겹치는지 체크
//            if (
//                    (startTime.isBefore(existingEnd) && startTime.isAfter(existingStart)) || // 새 예약 시작 시간이 기존 예약 사이에 있는 경우
//                    (endTime.isBefore(existingEnd) && endTime.isAfter(existingStart)) || // 새 예약 종료 시간이 기존 예약 사이에 있는 경우
//                    (startTime.isBefore(existingStart) && endTime.isAfter(existingEnd)) // 새 예약이 기존 예약을 완전히 포괄하는 경우
//            ) return true; // 중복되는 시간대가 있으므로 예약 불가
//        }
//        return false; // 중복되는 시간대가 없으므로 예약 가능
//    }

//    즉시사용등록
//    public Reservation createInstantReservation(ReservationCreateReqDto reservationCreateReqDto) {
//        Member member = this.findMemberByEmail();
//        Desk desk = this.findDeskByDeskNum(reservationCreateReqDto.getDeskNum());
//
//        // 현재 시간 기준으로 즉시 사용 가능 여부 확인
//        LocalDateTime now = LocalDateTime.now();
//        if (isReservationPossible(desk.getDeskNum(), now, reservationCreateReqDto.getMinutes()))
//            throw new IllegalStateException("해당 시간에는 예약이 불가능합니다.");
//
//        // 예약 객체 생성 및 저장 로직
//        Reservation reservation = Reservation.builder()
//                .reservationId(ReservationNumberGenerator.generateReservationNumber())
//                .member(member)
//                .desk(desk)
//                .status(Status.USING)
//                .startTime(reservationCreateReqDto.getStartTime())
//                .cntPeople(reservationCreateReqDto.getCntPeople())
//                .reservationTime(reservationCreateReqDto.getMinutes())
//                .endTime(reservationCreateReqDto.getStartTime().plusMinutes(reservationCreateReqDto.getMinutes()))
//                .build();
//        // 즉시 사용 등록에 따른 처리
//        desk.updateIsUsable("N");
//        return reservation;
//    }

//    예약등록
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

//        Status reservationStatus = Status.WAITING;
        Status reservationStatus;
        // 즉시 사용 요청인 경우, 예약 가능 여부를 다시 검증
        if (Duration.between(currentTime, reservationCreateReqDto.getStartTime()).abs().getSeconds() <= 1) {
            reservationStatus = Status.USING;
            desk.updateIsUsable("N"); // 즉시 사용으로 인해 테이블 상태 변경
        } else {
            reservationStatus = Status.WAITING; // 일반 예약인 경우
        }

        Reservation reservation = Reservation.builder()
                .reservationId(ReservationNumberGenerator.generateReservationNumber())
                .member(member)
                .desk(desk)
                .status(reservationStatus)
                .startTime(reservationCreateReqDto.getStartTime())
                .cntPeople(reservationCreateReqDto.getCntPeople())
                .reservationTime(reservationCreateReqDto.getMinutes())
                .endTime(reservationCreateReqDto.getStartTime().plusMinutes(reservationCreateReqDto.getMinutes()))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        if (reservationStatus == Status.USING) {
            // 즉시 사용인 경우에만 예약 종료 스케줄러 등록
            reservationScheduler.scheduleReservationEnd(savedReservation);
        } else {
            // 일반 예약인 경우, 예약 시작과 종료 스케줄러 등록
            reservationScheduler.scheduleReservationStart(savedReservation);
            reservationScheduler.scheduleReservationEnd(savedReservation);
        }
        return savedReservation;
    }
    public String cancelReservation(String reservationId) {
        Reservation reservation = this.findByReservationIdAndEmail(reservationId);
        if(reservation == null) throw new EntityNotFoundException("잘못된 예약번호입니다.");
        reservationScheduler.cancelFutureSchedule(reservationId);
        reservation.setStatus("CANCELED");
        return reservationRepository.save(reservation).getReservationId();
    }
    public List<ReservationResDto> getReservations(int deskNum, String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
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
