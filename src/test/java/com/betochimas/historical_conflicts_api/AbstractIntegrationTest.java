package com.betochimas.historical_conflicts_api;

import com.betochimas.historical_conflicts_api.auth.JwtService;
import com.betochimas.historical_conflicts_api.auth.Role;
import com.betochimas.historical_conflicts_api.auth.UserEntity;
import com.betochimas.historical_conflicts_api.auth.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected static final String TEST_USERNAME = "testuser";
    protected static final String TEST_PASSWORD = "testpass123";
    protected static final String TEST_EMAIL = "test@example.com";

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    static {
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CacheManager cacheManager;

    private String cachedToken;

    @BeforeEach
    void cleanState() {
        jdbcTemplate.execute(
            "TRUNCATE conflict_participants, leaders, battles, theaters, conflicts, nations, users RESTART IDENTITY CASCADE"
        );
        // Truncating the DB doesn't touch Redis — clear every cache so entries from one test
        // can't leak into the next (cross-test isolation).
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        cachedToken = null;
    }

    protected String authHeader() {
        return "Bearer " + token();
    }

    protected String token() {
        if (cachedToken == null) {
            registerTestUser();
            cachedToken = jwtService.issue(TEST_USERNAME, Role.USER).token();
        }
        return cachedToken;
    }

    private void registerTestUser() {
        UserEntity user = new UserEntity(
                TEST_USERNAME,
                TEST_EMAIL,
                passwordEncoder.encode(TEST_PASSWORD),
                Role.USER);
        userRepository.save(user);
    }
}
