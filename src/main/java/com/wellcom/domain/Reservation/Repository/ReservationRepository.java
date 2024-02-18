package com.wellcom.domain.Reservation.Repository;

import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findByReservationIdAndMember(String reservationId, Member member);
    Optional<Reservation> findByReservationId(String reservationId);
    @Query("SELECT r FROM Reservation r WHERE r.desk.deskNum = :deskNum AND r.startTime > :currentTime AND (r.status = 'WAITING' OR r.status = 'USING')")
    List<Reservation> findActiveReservationsByDeskAndTime(@Param("deskNum") int deskNum, @Param("currentTime") LocalDateTime currentTime);
    @Query("SELECT r FROM Reservation r WHERE r.desk.deskNum = :deskNum AND r.startTime >= :startOfDay AND r.endTime <= :endOfDay")
    List<Reservation> findReservationsByDeskAndTime(@Param("deskNum") int desk, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    // 회원별 예약 횟수 조회
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.member.id = :memberId")
    int countReservationsByMemberId(Long memberId);
    // 회원별 총 이용 시간 조회
    // 기존 메서드 정의를 다음과 같이 수정하거나, 새로운 메서드를 추가합니다.
    @Query("SELECT COALESCE(SUM(r.reservationTime), 0) FROM Reservation r WHERE r.member.id = :memberId")
    int sumReservationTimeByMemberId(@Param("memberId") Long memberId);
}
