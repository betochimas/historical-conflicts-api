package com.betochimas.historical_conflicts_api.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NationDto {
    private Long id;

    private String name;

    private String region;

    private Integer foundedYear;

    private Integer dissolvedYear;

    private String description;
}
