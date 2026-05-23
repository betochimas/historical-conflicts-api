package com.betochimas.historical_conflicts_api.repository;

import com.betochimas.historical_conflicts_api.domain.model.ConflictParticipantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConflictParticipantRepository extends JpaRepository<ConflictParticipantEntity, Long> {
    Page<ConflictParticipantEntity> findByConflictId(Long conflictId, Pageable pageable);
    Page<ConflictParticipantEntity> findByNationId(Long nationId, Pageable pageable);
}
