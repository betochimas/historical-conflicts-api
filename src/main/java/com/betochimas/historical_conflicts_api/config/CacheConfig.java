package com.betochimas.historical_conflicts_api.config;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
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
 *   <li><b>default (local dev):</b> Redis. Values are JSON via {@link JacksonJsonRedisSerializer}
 *       on the Spring-managed Jackson 3 {@link ObjectMapper}, so dates/enums round-trip exactly as
 *       at the web layer (e.g. {@code "1914-07-28"}); each cache is typed, so no {@code @class} hint.</li>
 *   <li><b>{@code prod} and {@code test}:</b> in-process Caffeine. Prod has no Redis service to run on
 *       Cloud Run; tests use Caffeine because Redis's {@code allEntries} clear() proved unreliable
 *       in this environment (flaky cache tests — see FU-1), and Caffeine is what prod actually runs.
 *       The cache abstraction ({@code @Cacheable}/{@code @CacheEvict}) and these cache names are
 *       identical across both stores; only the store changes.</li>
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
    public static final String THEATERS = "theaters";
    public static final String LEADERS = "leaders";

    private static final Duration ENTITY_TTL = Duration.ofMinutes(15);

    /**
     * Local-dev backend: customizes the auto-configured Redis cache manager (one typed, TTL'd
     * config per cache). Not active under {@code prod} or {@code test}, which use Caffeine instead.
     */
    @Bean
    @Profile("!prod & !test")
    public RedisCacheManagerBuilderCustomizer redisCacheCustomizer(ObjectMapper objectMapper) {
        return builder -> builder
                .withCacheConfiguration(NATIONS, entityCache(objectMapper, NationDto.class))
                .withCacheConfiguration(CONFLICTS, entityCache(objectMapper, ConflictDto.class))
                .withCacheConfiguration(BATTLES, entityCache(objectMapper, BattleDto.class))
                .withCacheConfiguration(CONFLICT_PARTICIPANTS, entityCache(objectMapper, ConflictParticipantDto.class))
                .withCacheConfiguration(THEATERS, entityCache(objectMapper, TheaterDto.class))
                .withCacheConfiguration(LEADERS, entityCache(objectMapper, LeaderDto.class));
    }

    private <T> RedisCacheConfiguration entityCache(ObjectMapper objectMapper, Class<T> type) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ENTITY_TTL)
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(objectMapper, type)));
    }

    /**
     * Prod + test backend: in-process Caffeine. Declaring an explicit {@link CacheManager} bean
     * makes Spring Boot's Redis cache auto-config back off, so no Redis cache manager is created.
     * The caches are fixed (no dynamic creation) and share the 15-min write TTL; null values
     * are not cached, matching {@code disableCachingNullValues()} above.
     */
    @Bean
    @Profile({"prod", "test"})
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                NATIONS, CONFLICTS, BATTLES, CONFLICT_PARTICIPANTS, THEATERS, LEADERS);
        manager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(ENTITY_TTL));
        manager.setAllowNullValues(false);
        return manager;
    }
}
