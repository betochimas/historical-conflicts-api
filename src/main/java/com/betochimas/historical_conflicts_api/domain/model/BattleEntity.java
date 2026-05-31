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
@Table(name = "battles")
public class BattleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "conflict_id", nullable = false)
    private ConflictEntity conflict;

    // Optional: the theater of its conflict this battle was fought in (nullable).
    @ManyToOne
    @JoinColumn(name = "theater_id")
    private TheaterEntity theater;

    @NonNull
    @Column(nullable = false)
    private String name;

    private LocalDate date;

    private String location;

    private String terrain;

    private String outcome;

    // Optional point geometry for map rendering (nullable — not every battle has a pin).
    private Double latitude;

    private Double longitude;

    @Column(length = 2000)
    private String description;
}
