package com.betochimas.historical_conflicts_api.repository;

import com.betochimas.historical_conflicts_api.domain.model.TreatyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatyRepository extends JpaRepository<TreatyEntity, Long> {
    Page<TreatyEntity> findByConflictId(Long conflictId, Pageable pageable);
}
