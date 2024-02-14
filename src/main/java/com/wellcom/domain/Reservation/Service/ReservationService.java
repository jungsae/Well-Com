package com.wellcom.domain.Reservation.Service;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Repository.DeskRepository;
import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.domain.Reservation.Dto.ReservationCreateReqDto;
import com.wellcom.domain.Reservation.Repository.ReservationRepository;
import com.wellcom.domain.Reservation.Reservation;
import com.wellcom.domain.Reservation.Status;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    public Reservation saveReservation(ReservationCreateReqDto reservationCreateReqDto) {
        Member member = this.findMemberByEmail();
        Desk desk = this.findDeskByDeskNum(reservationCreateReqDto.getDeskNum());
        LocalDateTime currentTime = LocalDateTime.now();

        if (!isReservationPossible(desk.getDeskNum(), reservationCreateReqDto.getStartTime(), reservationCreateReqDto.getMinutes()))
            throw new IllegalArgumentException("선택한 시간에 예약이 불가능합니다.");
        if (desk.getSeats() < reservationCreateReqDto.getCntPeople())
            throw new IllegalArgumentException("테이블 사용 가능 인원을 초과했습니다.");
        if (reservationCreateReqDto.getStartTime().isBefore(currentTime.minusSeconds(5L)))
            throw new IllegalArgumentException("잘못된 시간을 선택하셨습니다.");

        Status reservationStatus = Status.WAITING;

        Reservation reservation = Reservation.builder()
                .reservationId(ReservationNumberGenerator.generateReservationNumber())
                .member(member)
                .desk(desk)
                .status(reservationStatus) // 초기 상태 설정
                .startTime(reservationCreateReqDto.getStartTime())
                .cntPeople(reservationCreateReqDto.getCntPeople())
                .reservationTime(reservationCreateReqDto.getMinutes())
                .endTime(reservationCreateReqDto.getStartTime().plusMinutes(reservationCreateReqDto.getMinutes()))
                .build();
        // 즉시 사용
        if (Duration.between(currentTime, reservationCreateReqDto.getStartTime()).abs().getSeconds() <= 5) {
            reservation.setStatus("USING");
            desk.updateIsUsable("N");
            Reservation savedReservation = reservationRepository.save(reservation);
            reservationScheduler.scheduleReservationEnd(savedReservation);
            return savedReservation;
        } else {
            Reservation savedReservation = reservationRepository.save(reservation);
            reservationScheduler.scheduleReservationEnd(savedReservation);
            reservationScheduler.scheduleReservationStart(savedReservation);
            return savedReservation;
        }
    }
    public String cancelReservation(String reservationId) {
        Reservation reservation = this.findByReservationIdAndEmail(reservationId);
        if(reservation == null) throw new EntityNotFoundException("잘못된 예약번호입니다.");
        reservationScheduler.cancelFutureSchedule(reservationId);
        reservation.setStatus("CANCELED");
//        Desk desk =  reservation.getDesk();
//        desk.updateIsUsable("Y");
        return reservationRepository.save(reservation).getReservationId();
    }
}
