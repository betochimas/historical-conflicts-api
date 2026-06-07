package com.betochimas.historical_conflicts_api.domain.mapper;

import com.betochimas.historical_conflicts_api.domain.dto.AllianceMemberDto;
import com.betochimas.historical_conflicts_api.domain.model.AllianceMemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = CentralMapperConfig.class)
public interface AllianceMemberMapper {

    @Mapping(target = "allianceId", source = "alliance.id")
    @Mapping(target = "nationId", source = "nation.id")
    AllianceMemberDto toDto(AllianceMemberEntity entity);

    @Mapping(target = "alliance", source = "allianceId", qualifiedByName = "toAlliance")
    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    AllianceMemberEntity toEntity(AllianceMemberDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alliance", source = "allianceId", qualifiedByName = "toAlliance")
    @Mapping(target = "nation", source = "nationId", qualifiedByName = "toNation")
    void update(@MappingTarget AllianceMemberEntity entity, AllianceMemberDto dto);
}
