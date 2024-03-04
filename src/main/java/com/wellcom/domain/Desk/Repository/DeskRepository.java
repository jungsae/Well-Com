package com.wellcom.domain.Desk.Repository;

import com.wellcom.domain.Desk.Desk;
import com.wellcom.domain.Desk.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeskRepository extends JpaRepository<Desk, Long> {
    List<Desk> findByIsUsableAndHasTV(Status isUsable, Status hasTV);
    List<Desk> findByIsUsable(Status isUsable);
    List<Desk> findByHasTV(Status hasTV);
    Optional<Desk> findByDeskNum(int deskNum);
    @Query("SELECT DISTINCT d FROM Desk d left JOIN FETCH d.reservations r WHERE d.delYn = 'N'")
    List<Desk> findAllWithReservations();
}
