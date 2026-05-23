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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected static final String TEST_USERNAME = "testuser";
    protected static final String TEST_PASSWORD = "testpass123";
    protected static final String TEST_EMAIL = "test@example.com";

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String cachedToken;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute(
            "TRUNCATE conflict_participants, battles, conflicts, nations, users RESTART IDENTITY CASCADE"
        );
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
