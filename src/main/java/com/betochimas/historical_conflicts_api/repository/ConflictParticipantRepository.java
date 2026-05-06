package com.betochimas.historical_conflicts_api.repository;

import com.betochimas.historical_conflicts_api.domain.model.ConflictParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConflictParticipantRepository extends JpaRepository<ConflictParticipantEntity, Long> {
    List<ConflictParticipantEntity> findByConflictId(Long conflictId);
    List<ConflictParticipantEntity> findByNationId(Long nationId);
}
