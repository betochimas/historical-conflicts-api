package com.betochimas.historical_conflicts_api.domain.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Shared MapStruct config for all domain mappers: Spring beans, the {@link ReferenceResolver}
 * available for FK id&rarr;entity lookups, and {@code unmappedTargetPolicy = ERROR} so that adding a
 * column to an entity/DTO without mapping it fails the build (the class of bug behind the
 * SESSION_2026-06-03 "side" PUT-wipe).
 */
@MapperConfig(
        componentModel = "spring",
        uses = ReferenceResolver.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CentralMapperConfig {
}
