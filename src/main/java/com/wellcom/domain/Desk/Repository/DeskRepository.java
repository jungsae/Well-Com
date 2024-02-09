package com.wellcom.domain.Desk.Repository;

import com.wellcom.domain.Desk.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeskRepository extends JpaRepository<Desk, Long> {
}
