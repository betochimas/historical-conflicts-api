package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.AllianceDto;
import com.betochimas.historical_conflicts_api.domain.model.AllianceEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface AllianceMapper {

    AllianceDto toDto(AllianceEntity entity);

    AllianceEntity toEntity(AllianceDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget AllianceEntity entity, AllianceDto dto);
}
