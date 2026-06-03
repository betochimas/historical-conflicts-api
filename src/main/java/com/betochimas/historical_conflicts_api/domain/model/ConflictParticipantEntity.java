package com.betochimas.historical_conflicts_api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "conflict_participants")
public class ConflictParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "conflict_id", nullable = false)
    private ConflictEntity conflict;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "nation_id", nullable = false)
    private NationEntity nation;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantRole role;

    private Integer troopsCommitted;

    private Integer casualties;

    private String outcome;

    /** Coalition / belligerent group this participant fought on (e.g. "Allied Powers"). Free text, nullable. */
    private String side;
}
