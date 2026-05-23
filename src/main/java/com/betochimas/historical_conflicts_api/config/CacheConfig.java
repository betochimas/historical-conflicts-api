package com.betochimas.historical_conflicts_api.config;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/**
 * Redis-backed cache configuration. One cache per entity type, each holding a single DTO type
 * (we only cache {@code findOne(id)} lookups — see PHASE_2C_REDIS_DESIGN.md, decision A).
 *
 * <p>Values are serialized as JSON via {@link JacksonJsonRedisSerializer} built on the
 * Spring-managed Jackson 3 {@link ObjectMapper}, so dates/enums round-trip exactly as they do
 * at the web layer (e.g. {@code "1914-07-28"}). Because each cache is typed, no polymorphic
 * {@code @class} type hint is stored.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String NATIONS = "nations";
    public static final String CONFLICTS = "conflicts";
    public static final String BATTLES = "battles";
    public static final String CONFLICT_PARTICIPANTS = "conflictParticipants";

    private static final Duration ENTITY_TTL = Duration.ofMinutes(15);

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheCustomizer(ObjectMapper objectMapper) {
        return builder -> builder
                .withCacheConfiguration(NATIONS, entityCache(objectMapper, NationDto.class))
                .withCacheConfiguration(CONFLICTS, entityCache(objectMapper, ConflictDto.class))
                .withCacheConfiguration(BATTLES, entityCache(objectMapper, BattleDto.class))
                .withCacheConfiguration(CONFLICT_PARTICIPANTS, entityCache(objectMapper, ConflictParticipantDto.class));
    }

    private <T> RedisCacheConfiguration entityCache(ObjectMapper objectMapper, Class<T> type) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ENTITY_TTL)
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(objectMapper, type)));
    }
}
