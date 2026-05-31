package com.betochimas.historical_conflicts_api.repository;

import com.betochimas.historical_conflicts_api.domain.model.TheaterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterRepository extends JpaRepository<TheaterEntity, Long> {
    Page<TheaterEntity> findByConflictId(Long conflictId, Pageable pageable);

    List<TheaterEntity> findByConflictId(Long conflictId);
}
