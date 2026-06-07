package com.betochimas.historical_conflicts_api.domain.dto;

import com.betochimas.historical_conflicts_api.domain.model.AllianceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllianceDto {
    private Long id;

    @NotBlank(message = "name is required")
    @Size(max = 255)
    private String name;

    @NotNull(message = "allianceType is required")
    private AllianceType allianceType;

    private LocalDate formedDate;
    private LocalDate dissolvedDate;

    @Size(max = 2000)
    private String description;
}
