package com.betochimas.historical_conflicts_api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BattleDto {
    private Long id;
    private Long conflictId;
    private Long theaterId;
    private String name;
    private LocalDate date;
    private String location;
    private String terrain;
    private String outcome;
    private Double latitude;
    private Double longitude;
    private String description;
}
