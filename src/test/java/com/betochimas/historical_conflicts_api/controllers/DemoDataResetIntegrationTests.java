package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.config.DemoDataResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the nightly demo reset restores the seeded domain data after it's been cleared.
 * {@code cleanState()} truncates every domain table before each test, so calling reset() and
 * then finding the full WWI seed back proves the {@code db/demo/demo_reset.sql} script runs.
 */
public class DemoDataResetIntegrationTests extends AbstractIntegrationTest {

    private final DemoDataResetService resetService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DemoDataResetIntegrationTests(DemoDataResetService resetService, JdbcTemplate jdbcTemplate) {
        this.resetService = resetService;
        this.jdbcTemplate = jdbcTemplate;
    }

    private int count(String table) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    }

    @Test
    public void testThatResetRestoresSeededDomainData() {
        // cleanState() truncated everything; confirm the starting point is empty.
        assertEquals(0, count("nations"));

        resetService.reset();

        assertEquals(7, count("nations"));
        assertEquals(1, count("conflicts"));
        assertEquals(5, count("battles"));
        assertEquals(3, count("theaters"));
        assertEquals(7, count("leaders"));
        assertEquals(7, count("conflict_participants"));
    }

    @Test
    public void testThatResetIsIdempotent() {
        resetService.reset();
        resetService.reset();

        // TRUNCATE ... RESTART IDENTITY at the top of the script means a second run doesn't
        // duplicate rows.
        assertEquals(7, count("nations"));
        assertEquals(7, count("conflict_participants"));
    }
}
