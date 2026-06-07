package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.config.EntityNotFoundException;
import com.betochimas.historical_conflicts_api.domain.model.AllianceEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.domain.model.NationEntity;
import com.betochimas.historical_conflicts_api.domain.model.TheaterEntity;
import com.betochimas.historical_conflicts_api.domain.model.TreatyEntity;
import com.betochimas.historical_conflicts_api.repository.AllianceRepository;
import com.betochimas.historical_conflicts_api.repository.ConflictRepository;
import com.betochimas.historical_conflicts_api.repository.NationRepository;
import com.betochimas.historical_conflicts_api.repository.TheaterRepository;
import com.betochimas.historical_conflicts_api.repository.TreatyRepository;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * Resolves a flattened FK id from a DTO into the managed entity reference. A {@code null} id maps
 * to {@code null} (legitimate for nullable FKs; for PATCH the {@code IGNORE} strategy means this is
 * never reached); a non-existent id throws {@link EntityNotFoundException} (&rarr; 404). Wired into
 * the mappers through {@code uses = ReferenceResolver.class} + {@code qualifiedByName}.
 */
@Component
public class ReferenceResolver {

    private final ConflictRepository conflicts;
    private final NationRepository nations;
    private final TheaterRepository theaters;
    private final AllianceRepository alliances;
    private final TreatyRepository treaties;

    public ReferenceResolver(ConflictRepository conflicts,
                             NationRepository nations,
                             TheaterRepository theaters,
                             AllianceRepository alliances,
                             TreatyRepository treaties) {
        this.conflicts = conflicts;
        this.nations = nations;
        this.theaters = theaters;
        this.alliances = alliances;
        this.treaties = treaties;
    }

    @Named("toConflict")
    public ConflictEntity toConflict(Long id) {
        if (id == null) {
            return null;
        }
        return conflicts.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conflict", id));
    }

    @Named("toNation")
    public NationEntity toNation(Long id) {
        if (id == null) {
            return null;
        }
        return nations.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nation", id));
    }

    @Named("toTheater")
    public TheaterEntity toTheater(Long id) {
        if (id == null) {
            return null;
        }
        return theaters.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Theater", id));
    }

    @Named("toAlliance")
    public AllianceEntity toAlliance(Long id) {
        if (id == null) {
            return null;
        }
        return alliances.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alliance", id));
    }

    @Named("toTreaty")
    public TreatyEntity toTreaty(Long id) {
        if (id == null) {
            return null;
        }
        return treaties.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Treaty", id));
    }
}
