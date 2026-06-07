package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import com.betochimas.historical_conflicts_api.domain.model.TheaterEntity;
import com.betochimas.historical_conflicts_api.repository.BattleRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = CentralMapperConfig.class)
public abstract class TheaterMapper {

    // MapStruct subclasses this @Component; field injection is the standard way to give an
    // abstract Spring mapper a collaborator (it can't use the generated constructor).
    @Autowired
    protected BattleRepository battleRepository;

    @Mapping(target = "conflictId", source = "conflict.id")
    @Mapping(target = "battleIds", ignore = true) // derived below, in @AfterMapping
    public abstract TheaterDto toDto(TheaterEntity entity);

    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    public abstract TheaterEntity toEntity(TheaterDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conflict", source = "conflictId", qualifiedByName = "toConflict")
    public abstract void update(@MappingTarget TheaterEntity entity, TheaterDto dto);

    @AfterMapping
    protected void fillBattleIds(@MappingTarget TheaterDto dto, TheaterEntity entity) {
        dto.setBattleIds(battleRepository.findIdsByTheaterId(entity.getId()));
    }
}
