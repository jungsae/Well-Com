package com.wellcom.domain.Record.Repository;

import com.wellcom.domain.Record.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    long countByMemberIdAndIsWinner(Long memberId, String isWinner);
    long countByMemberId(Long memberId);
}
