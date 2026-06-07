package com.betochimas.historical_conflicts_api.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllianceMemberDto {
    private Long id;

    @NotNull(message = "allianceId is required")
    private Long allianceId;

    @NotNull(message = "nationId is required")
    private Long nationId;

    private LocalDate joinedDate;
    private LocalDate leftDate;
}
