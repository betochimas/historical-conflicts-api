package com.betochimas.historical_conflicts_api.service.impl;

import com.betochimas.historical_conflicts_api.config.EntityNotFoundException;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto.AtlasBattle;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto.AtlasConflict;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto.AtlasParticipant;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto.AtlasStats;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictAtlasDto.AtlasTheater;
import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictParticipantEntity;
import com.betochimas.historical_conflicts_api.domain.model.TheaterEntity;
import com.betochimas.historical_conflicts_api.repository.BattleRepository;
import com.betochimas.historical_conflicts_api.repository.ConflictParticipantRepository;
import com.betochimas.historical_conflicts_api.repository.ConflictRepository;
import com.betochimas.historical_conflicts_api.repository.TheaterRepository;
import com.betochimas.historical_conflicts_api.service.ConflictAtlasService;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ConflictAtlasServiceImpl implements ConflictAtlasService {

    private final ConflictRepository conflictRepository;
    private final BattleRepository battleRepository;
    private final TheaterRepository theaterRepository;
    private final ConflictParticipantRepository participantRepository;

    public ConflictAtlasServiceImpl(ConflictRepository conflictRepository,
                                    BattleRepository battleRepository,
                                    TheaterRepository theaterRepository,
                                    ConflictParticipantRepository participantRepository) {
        this.conflictRepository = conflictRepository;
        this.battleRepository = battleRepository;
        this.theaterRepository = theaterRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    // Deliberately not cached: the atlas is a cheap per-conflict aggregate (a handful of indexed
    // queries), and caching it would require cross-entity invalidation on every battle/theater/
    // participant/conflict write — including deletes, where the conflict id isn't in scope. The
    // payoff isn't worth that fragility; recomputing per request is fast and always correct.
    public ConflictAtlasDto getAtlas(Long conflictId) {
        ConflictEntity conflict = conflictRepository.findById(conflictId)
                .orElseThrow(() -> new EntityNotFoundException("Conflict", conflictId));

        // Battles sorted ascending by date, nulls last; seq is the 1-based position after sorting.
        List<BattleEntity> battles = battleRepository.findByConflictId(conflictId);
        battles.sort(Comparator.comparing(BattleEntity::getDate,
                Comparator.nullsLast(Comparator.naturalOrder())));
        List<AtlasBattle> atlasBattles = new java.util.ArrayList<>(battles.size());
        for (int i = 0; i < battles.size(); i++) {
            atlasBattles.add(toAtlasBattle(battles.get(i), i + 1));
        }

        // Group battle ids by theater (in the date-sorted encounter order above).
        Map<Long, List<Long>> battleIdsByTheater = battles.stream()
                .filter(b -> b.getTheater() != null)
                .collect(Collectors.groupingBy(b -> b.getTheater().getId(),
                        Collectors.mapping(BattleEntity::getId, Collectors.toList())));

        List<AtlasTheater> atlasTheaters = theaterRepository.findByConflictId(conflictId).stream()
                .map(t -> toAtlasTheater(t, battleIdsByTheater.getOrDefault(t.getId(), List.of())))
                .toList();

        List<ConflictParticipantEntity> participants = participantRepository.findByConflictId(conflictId);
        List<AtlasParticipant> atlasParticipants = participants.stream()
                .map(this::toAtlasParticipant)
                .toList();

        AtlasStats stats = computeStats(conflict, battles.size(), atlasTheaters.size(), participants);

        return ConflictAtlasDto.builder()
                .conflict(toAtlasConflict(conflict))
                .theaters(atlasTheaters)
                .battles(atlasBattles)
                .participants(atlasParticipants)
                .stats(stats)
                .build();
    }

    private AtlasStats computeStats(ConflictEntity conflict, int totalBattles, int totalTheaters,
                                    List<ConflictParticipantEntity> participants) {
        long totalCasualties = participants.stream()
                .map(ConflictParticipantEntity::getCasualties)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
        long totalTroops = participants.stream()
                .map(ConflictParticipantEntity::getTroopsCommitted)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
        Long durationDays = (conflict.getStartDate() != null && conflict.getEndDate() != null)
                ? ChronoUnit.DAYS.between(conflict.getStartDate(), conflict.getEndDate())
                : null;
        return AtlasStats.builder()
                .totalBattles(totalBattles)
                .totalTheaters(totalTheaters)
                .totalParticipants(participants.size())
                .totalCasualties(totalCasualties)
                .totalTroopsCommitted(totalTroops)
                .durationDays(durationDays)
                .build();
    }

    private AtlasConflict toAtlasConflict(ConflictEntity c) {
        return AtlasConflict.builder()
                .id(c.getId())
                .name(c.getName())
                .conflictType(c.getConflictType())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .outcome(c.getOutcome())
                .build();
    }

    private AtlasTheater toAtlasTheater(TheaterEntity t, List<Long> battleIds) {
        return AtlasTheater.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .battleIds(battleIds)
                .build();
    }

    private AtlasBattle toAtlasBattle(BattleEntity b, int seq) {
        return AtlasBattle.builder()
                .seq(seq)
                .id(b.getId())
                .name(b.getName())
                .date(b.getDate())
                .latitude(b.getLatitude())
                .longitude(b.getLongitude())
                .theaterId(b.getTheater() != null ? b.getTheater().getId() : null)
                .outcome(b.getOutcome())
                .description(b.getDescription())
                .build();
    }

    private AtlasParticipant toAtlasParticipant(ConflictParticipantEntity p) {
        return AtlasParticipant.builder()
                .nationId(p.getNation().getId())
                .nationName(p.getNation().getName())
                .region(p.getNation().getRegion())
                .role(p.getRole())
                .troopsCommitted(p.getTroopsCommitted())
                .casualties(p.getCasualties())
                .build();
    }
}
