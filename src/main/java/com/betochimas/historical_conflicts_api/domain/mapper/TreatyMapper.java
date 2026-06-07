package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.TreatyDto;
import com.betochimas.historical_conflicts_api.domain.model.TreatyEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface TreatyMapper {

    @Mapping(target = "conflictId", source = "conflict.id")
    TreatyDto toDto(TreatyEntity entity);

    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    TreatyEntity toEntity(TreatyDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    void update(@MappingTarget TreatyEntity entity, TreatyDto dto);
}
