package com.wellcom.domain.SharingRoom.Repository;

import com.wellcom.domain.Item.ItemStatus;
import com.wellcom.domain.SharingRoom.SharingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharingRoomRepository extends JpaRepository<SharingRoom, Long> {
    List<SharingRoom> findByItemItemStatus(ItemStatus itemStatus);

    @Query("SELECT COUNT(s) FROM SharingRoom s WHERE s.member.id = :memberId")
    int countByMemberId(@Param("memberId") Long memberId);
}
