package com.betochimas.historical_conflicts_api.config;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/**
 * Cache configuration. One cache per entity type — we only cache {@code findOne(id)} lookups
 * (see PHASE_2C_REDIS_DESIGN.md, decision A). 15-min TTL, nulls not cached, evicted on write.
 *
 * <p>The backend is profile-scoped:
 * <ul>
 *   <li><b>default / test (local):</b> Redis. Values are JSON via {@link JacksonJsonRedisSerializer}
 *       on the Spring-managed Jackson 3 {@link ObjectMapper}, so dates/enums round-trip exactly as
 *       at the web layer (e.g. {@code "1914-07-28"}); each cache is typed, so no {@code @class} hint.</li>
 *   <li><b>{@code prod}:</b> in-process Caffeine — no Redis service to run on Cloud Run. The cache
 *       abstraction ({@code @Cacheable}/{@code @CacheEvict}) and these four cache names are identical;
 *       only the store changes.</li>
 * </ul>
 * In {@code prod}, also set {@code management.health.redis.enabled=false} so the actuator health
 * check (used by Cloud Run) doesn't try to ping a Redis that isn't there.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String NATIONS = "nations";
    public static final String CONFLICTS = "conflicts";
    public static final String BATTLES = "battles";
    public static final String CONFLICT_PARTICIPANTS = "conflictParticipants";

    private static final Duration ENTITY_TTL = Duration.ofMinutes(15);

    /**
     * Default/test backend: customizes the auto-configured Redis cache manager (one typed,
     * TTL'd config per cache). Not active under {@code prod}, where Caffeine is used instead.
     */
    @Bean
    @Profile("!prod")
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

    /**
     * Prod backend: in-process Caffeine. Declaring an explicit {@link CacheManager} bean makes
     * Spring Boot's Redis cache auto-config back off, so no Redis cache manager is created.
     * The four caches are fixed (no dynamic creation) and share the 15-min write TTL; null values
     * are not cached, matching {@code disableCachingNullValues()} above.
     */
    @Bean
    @Profile("prod")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                NATIONS, CONFLICTS, BATTLES, CONFLICT_PARTICIPANTS);
        manager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(ENTITY_TTL));
        manager.setAllowNullValues(false);
        return manager;
    }
}
