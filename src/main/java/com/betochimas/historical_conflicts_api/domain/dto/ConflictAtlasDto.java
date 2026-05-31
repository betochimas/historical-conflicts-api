package com.betochimas.historical_conflicts_api.domain.dto;

import com.betochimas.historical_conflicts_api.domain.model.ConflictType;
import com.betochimas.historical_conflicts_api.domain.model.ParticipantRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Aggregate "atlas" of a single conflict: everything the interactive map + timeline needs in
 * one payload (see Shared/INTERACTIVEMAP_TIMELINE_DESIGN.md, slice B2). Battles are sorted
 * ascending by date (nulls last) and carry a 1-based {@code seq} so the timeline can step
 * through them directly. Participants are denormalized with the nation's name/region so the
 * frontend needs no follow-up nation lookups. Stats are computed in the service from the
 * already-fetched rows — no extra queries.
 *
 * <p>This is the frozen [H1] contract the frontend mirrors in {@code conflictsApi.ts}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConflictAtlasDto {

    private AtlasConflict conflict;
    private List<AtlasTheater> theaters;
    private List<AtlasBattle> battles;
    private List<AtlasParticipant> participants;
    private AtlasStats stats;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AtlasConflict {
        private Long id;
        private String name;
        private ConflictType conflictType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String outcome;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AtlasTheater {
        private Long id;
        private String name;
        private String description;
        /** Member battle ids, in the same date-sorted order as {@link #battles}. */
        private List<Long> battleIds;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AtlasBattle {
        /** 1-based index after sorting by date (nulls last). Stable handle for the timeline. */
        private int seq;
        private Long id;
        private String name;
        private LocalDate date;
        private Double latitude;
        private Double longitude;
        private Long theaterId;
        private String outcome;
        private String description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AtlasParticipant {
        private Long nationId;
        private String nationName;
        private String region;
        private ParticipantRole role;
        private Integer troopsCommitted;
        private Integer casualties;
    }

    /**
     * Conflict-level aggregates. Casualty/troop totals come from {@code conflict_participants}
     * (battles carry no casualty figures in the current schema), summing non-null values.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AtlasStats {
        private int totalBattles;
        private int totalTheaters;
        private int totalParticipants;
        private long totalCasualties;
        private long totalTroopsCommitted;
        /** Whole days from start to end; null if either conflict date is missing. */
        private Long durationDays;
    }
}
