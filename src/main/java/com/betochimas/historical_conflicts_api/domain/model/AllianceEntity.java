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
@Table(name = "alliances")
public class AllianceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllianceType allianceType;

    private LocalDate formedDate;

    private LocalDate dissolvedDate;

    @Column(length = 2000)
    private String description;
}
