package com.betochimas.historical_conflicts_api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "alliance_members")
public class AllianceMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "alliance_id", nullable = false)
    private AllianceEntity alliance;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "nation_id", nullable = false)
    private NationEntity nation;

    private LocalDate joinedDate;

    private LocalDate leftDate;
}
