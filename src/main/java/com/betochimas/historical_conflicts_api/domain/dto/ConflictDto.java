package com.betochimas.historical_conflicts_api.domain.dto;

import com.betochimas.historical_conflicts_api.domain.model.ConflictType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConflictDto {
    private Long id;
    private String name;
    private ConflictType conflictType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String outcome;
    private String description;
}
