package com.betochimas.historical_conflicts_api.domain.dto;

import com.betochimas.historical_conflicts_api.domain.model.ParticipantRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConflictParticipantDto {
    private Long id;
    private Long conflictId;
    private Long nationId;
    private ParticipantRole role;
    private Integer troopsCommitted;
    private Integer casualties;
    private String outcome;
}
