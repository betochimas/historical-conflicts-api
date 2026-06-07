package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.model.ConflictParticipantEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface ConflictParticipantMapper {

    @Mapping(target = "conflictId", source = "conflict.id")
    @Mapping(target = "nationId", source = "nation.id")
    ConflictParticipantDto toDto(ConflictParticipantEntity entity);

    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    ConflictParticipantEntity toEntity(ConflictParticipantDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    void update(@MappingTarget ConflictParticipantEntity entity, ConflictParticipantDto dto);
}
