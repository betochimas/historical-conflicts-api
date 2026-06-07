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
@Table(name = "treaties")
public class TreatyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional: the conflict this treaty concluded (nullable — some treaties aren't tied to a
    // specific conflict in our data). A conflict can have several treaties (armistice + peace).
    @ManyToOne
    @JoinColumn(name = "conflict_id")
    private ConflictEntity conflict;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatyType treatyType;

    private LocalDate signedDate;

    private String location;

    @Column(length = 2000)
    private String description;
}
