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
@Table(name = "leaders")
public class LeaderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "nation_id", nullable = false)
    private NationEntity nation;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaderRole role;

    private String title;

    private Integer birthYear;

    private Integer deathYear;

    @Column(length = 2000)
    private String description;
}
