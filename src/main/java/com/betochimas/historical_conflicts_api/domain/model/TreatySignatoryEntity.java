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
@Table(name = "treaty_signatories")
public class TreatySignatoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "treaty_id", nullable = false)
    private TreatyEntity treaty;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "nation_id", nullable = false)
    private NationEntity nation;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignatoryRole role;

    private LocalDate ratifiedDate;
}
