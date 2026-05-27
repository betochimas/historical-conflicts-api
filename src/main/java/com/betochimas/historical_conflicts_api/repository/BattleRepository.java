package com.betochimas.historical_conflicts_api.repository;

import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BattleRepository extends JpaRepository<BattleEntity, Long> {
    Page<BattleEntity> findByConflictId(Long conflictId, Pageable pageable);

    Page<BattleEntity> findByTheaterId(Long theaterId, Pageable pageable);

    @Query("select b.id from BattleEntity b where b.theater.id = :theaterId")
    List<Long> findIdsByTheaterId(@Param("theaterId") Long theaterId);
}
