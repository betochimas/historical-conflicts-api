package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.TreatySignatoryDto;
import com.betochimas.historical_conflicts_api.domain.model.TreatySignatoryEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface TreatySignatoryMapper {

    @Mapping(target = "treatyId", source = "treaty.id")
    @Mapping(target = "nationId", source = "nation.id")
    TreatySignatoryDto toDto(TreatySignatoryEntity entity);

    @Mapping(target = "treaty", source = "treatyId", qualifiedByName = "toTreaty")
    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    TreatySignatoryEntity toEntity(TreatySignatoryDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treaty", source = "treatyId", qualifiedByName = "toTreaty")
    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    void update(@MappingTarget TreatySignatoryEntity entity, TreatySignatoryDto dto);
}
