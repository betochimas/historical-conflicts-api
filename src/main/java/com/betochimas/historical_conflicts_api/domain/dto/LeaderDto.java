package com.betochimas.historical_conflicts_api.domain.dto;

import com.betochimas.historical_conflicts_api.domain.model.LeaderRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaderDto {
    private Long id;

    @NotNull(message = "nationId is required")
    private Long nationId;

    @NotBlank(message = "name is required")
    @Size(max = 255)
    private String name;

    @NotNull(message = "role is required")
    private LeaderRole role;

    @Size(max = 255)
    private String title;

    private Integer birthYear;
    private Integer deathYear;

    @Size(max = 2000)
    private String description;
}
