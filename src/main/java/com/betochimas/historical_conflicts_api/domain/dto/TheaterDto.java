package com.betochimas.historical_conflicts_api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TheaterDto {
    private Long id;

    @NotNull(message = "conflictId is required")
    private Long conflictId;

    @NotBlank(message = "name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String region;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = 255)
    private String outcome;

    @Size(max = 2000)
    private String description;

    /**
     * The battles in this theater. Read-back on every response; on write (create/full/partial
     * update) it is additive/reassign — listed battles are attached or moved to this theater,
     * unlisted battles are left untouched.
     */
    private List<Long> battleIds;
}
