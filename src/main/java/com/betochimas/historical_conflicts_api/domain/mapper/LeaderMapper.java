package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.domain.model.LeaderEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface LeaderMapper {

    @Mapping(target = "nationId", source = "nation.id")
    LeaderDto toDto(LeaderEntity entity);

    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    LeaderEntity toEntity(LeaderDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    void update(@MappingTarget LeaderEntity entity, LeaderDto dto);
}
