package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface BattleMapper {

    @Mapping(target = "conflictId", source = "conflict.id")
    @Mapping(target = "theaterId", source = "theater.id")
    BattleDto toDto(BattleEntity entity);

    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    @Mapping(target = "theater", source = "theaterId", qualifiedByName = "toTheater")
    BattleEntity toEntity(BattleDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    @Mapping(target = "theater", source = "theaterId", qualifiedByName = "toTheater")
    void update(@MappingTarget BattleEntity entity, BattleDto dto);
}
