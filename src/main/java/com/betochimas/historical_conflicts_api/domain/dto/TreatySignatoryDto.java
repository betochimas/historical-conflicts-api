package com.betochimas.historical_conflicts_api.domain.dto;

import com.betochimas.historical_conflicts_api.domain.model.SignatoryRole;
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
public class TreatySignatoryDto {
    private Long id;

    @NotNull(message = "treatyId is required")
    private Long treatyId;

    @NotNull(message = "nationId is required")
    private Long nationId;

    @NotNull(message = "role is required")
    private SignatoryRole role;

    private LocalDate ratifiedDate;
}
