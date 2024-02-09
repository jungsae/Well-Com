package com.wellcom.domain.SharingRoom.Repository;

import com.wellcom.domain.SharingRoom.SharingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharingRoomRepository extends JpaRepository<SharingRoom, Long> {

}
