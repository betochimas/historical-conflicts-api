package com.betochimas.historical_conflicts_api.domain.dto;

import com.betochimas.historical_conflicts_api.domain.model.TreatyType;
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
public class TreatyDto {
    private Long id;

    // Nullable: a treaty need not be tied to a specific conflict (the lone nullable FK).
    private Long conflictId;

    @NotBlank(message = "name is required")
    @Size(max = 255)
    private String name;

    @NotNull(message = "treatyType is required")
    private TreatyType treatyType;

    private LocalDate signedDate;

    @Size(max = 255)
    private String location;

    @Size(max = 2000)
    private String description;
}
