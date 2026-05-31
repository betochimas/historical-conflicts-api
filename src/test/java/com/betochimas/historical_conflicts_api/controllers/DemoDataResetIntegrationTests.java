package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.config.DemoDataResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the nightly demo reset restores the seeded domain data after it's been cleared.
 * {@code cleanState()} truncates every domain table before each test, so calling reset() and
 * then finding the full WWI seed back proves the {@code db/demo/demo_reset.sql} script runs.
 */
public class DemoDataResetIntegrationTests extends AbstractIntegrationTest {

    private final DemoDataResetService resetService;
    private final JdbcTemplate jdbcTemplate;
    private final MockMvc mockMvc;

    @Autowired
    public DemoDataResetIntegrationTests(DemoDataResetService resetService, JdbcTemplate jdbcTemplate,
                                         MockMvc mockMvc) {
        this.resetService = resetService;
        this.jdbcTemplate = jdbcTemplate;
        this.mockMvc = mockMvc;
    }

    private int count(String table) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    }

    @Test
    public void testThatResetRestoresSeededDomainData() {
        // cleanState() truncated everything; confirm the starting point is empty.
        assertEquals(0, count("nations"));

        resetService.reset();

        // WWI + Russo-Japanese War (the latter added in V14 / B3).
        assertEquals(8, count("nations"));               // 7 WWI + Empire of Japan
        assertEquals(2, count("conflicts"));             // WWI + Russo-Japanese War
        assertEquals(12, count("battles"));              // 5 WWI + 7 Russo-Japanese
        assertEquals(5, count("theaters"));              // 3 WWI + 2 Russo-Japanese
        assertEquals(7, count("leaders"));               // WWI only (leaders deferred for RJW)
        assertEquals(9, count("conflict_participants")); // 7 WWI + 2 Russo-Japanese
    }

    @Test
    public void testThatResetIsIdempotent() {
        resetService.reset();
        resetService.reset();

        // TRUNCATE ... RESTART IDENTITY at the top of the script means a second run doesn't
        // duplicate rows.
        assertEquals(8, count("nations"));
        assertEquals(9, count("conflict_participants"));
    }

    /**
     * End-to-end check of the seeded Russo-Japanese War (B3) through the {@code /atlas} endpoint —
     * the integration suite normally truncates seed data, so reset() is the only way to exercise it.
     * Confirms battle count, date-sort + contiguous seq, coordinates, theater grouping, and stats.
     */
    @Test
    public void testThatSeededRussoJapaneseWarProducesACorrectAtlas() throws Exception {
        resetService.reset();
        Long id = jdbcTemplate.queryForObject(
                "SELECT id FROM conflicts WHERE name = 'Russo-Japanese War'", Long.class);

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conflict.name").value("Russo-Japanese War"))
                .andExpect(jsonPath("$.conflict.conflictType").value("WAR"))
                .andExpect(jsonPath("$.battles.length()").value(7))
                .andExpect(jsonPath("$.theaters.length()").value(2))
                .andExpect(jsonPath("$.participants.length()").value(2))
                // Sorted ascending by date: Port Arthur (1904-02-09) first, Tsushima (1905-05-27) last.
                .andExpect(jsonPath("$.battles[0].name").value("Battle of Port Arthur"))
                .andExpect(jsonPath("$.battles[0].seq").value(1))
                .andExpect(jsonPath("$.battles[0].latitude").value(38.81))
                .andExpect(jsonPath("$.battles[0].longitude").value(121.26))
                .andExpect(jsonPath("$.battles[6].name").value("Battle of Tsushima"))
                .andExpect(jsonPath("$.battles[6].seq").value(7))
                // Battles are assigned to theaters (Port Arthur → Naval, Mukden → Manchurian).
                .andExpect(jsonPath("$.battles[0].theaterId").isNumber())
                .andExpect(jsonPath("$.battles[5].theaterId").isNumber())
                .andExpect(jsonPath("$.stats.totalBattles").value(7))
                .andExpect(jsonPath("$.stats.totalTheaters").value(2))
                .andExpect(jsonPath("$.stats.totalParticipants").value(2))
                .andExpect(jsonPath("$.stats.totalCasualties").value(410_000))        // 240k + 170k
                .andExpect(jsonPath("$.stats.totalTroopsCommitted").value(2_565_000)) // 1.2M + 1.365M
                .andExpect(jsonPath("$.stats.durationDays").isNumber());
    }
}
