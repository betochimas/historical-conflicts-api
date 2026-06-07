package com.betochimas.historical_conflicts_api.repository;

import com.betochimas.historical_conflicts_api.domain.model.AllianceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllianceRepository extends JpaRepository<AllianceEntity, Long> {
}
