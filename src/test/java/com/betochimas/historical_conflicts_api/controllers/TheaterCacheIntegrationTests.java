package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import com.betochimas.historical_conflicts_api.repository.TheaterRepository;
import com.betochimas.historical_conflicts_api.service.BattleService;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import com.betochimas.historical_conflicts_api.service.TheaterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Theater cache behaviour: a cache hit skips the repository, a write evicts the entry, and — because
 * a theater's {@code battleIds} is derived — a battle write cross-evicts the theater cache so the
 * next read reflects the new membership.
 */
public class TheaterCacheIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final TheaterService theaterService;
    private final ConflictService conflictService;
    private final BattleService battleService;
    private final ObjectMapper objectMapper;

    @MockitoSpyBean
    private TheaterRepository theaterRepository;

    @Autowired
    public TheaterCacheIntegrationTests(MockMvc mockMvc, TheaterService theaterService,
                                        ConflictService conflictService, BattleService battleService,
                                        ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.theaterService = theaterService;
        this.conflictService = conflictService;
        this.battleService = battleService;
        this.objectMapper = objectMapper;
    }

    private ConflictDto savedConflict() {
        return conflictService.create(TestDataUtil.createTestConflictDtoA());
    }

    @Test
    public void testThatSecondGetIsServedFromCacheAndDoesNotHitRepository() throws Exception {
        Long id = theaterService.create(TestDataUtil.createTestTheaterDtoA(savedConflict().getId())).getId();

        mockMvc.perform(get("/api/theaters/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/theaters/" + id)).andExpect(status().isOk());

        verify(theaterRepository, times(1)).findById(id);
    }

    @Test
    public void testThatUpdateEvictsCacheSoNextGetHitsRepositoryAgain() throws Exception {
        TheaterDto created = theaterService.create(TestDataUtil.createTestTheaterDtoA(savedConflict().getId()));
        Long id = created.getId();

        mockMvc.perform(get("/api/theaters/" + id)).andExpect(status().isOk());           // miss → findById #1

        created.setOutcome("Stalemate");
        mockMvc.perform(put("/api/theaters/" + id)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk());                                               // evicts the entry

        mockMvc.perform(get("/api/theaters/" + id)).andExpect(status().isOk());           // miss again → findById #2

        verify(theaterRepository, times(2)).findById(id);
    }

    @Test
    public void testThatAssigningABattleCrossEvictsTheaterCacheSoBattleIdsRefresh() throws Exception {
        ConflictDto conflict = savedConflict();
        Long theaterId = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflict.getId())).getId();

        // Prime the theater cache while it has no battles.
        mockMvc.perform(get("/api/theaters/" + theaterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.battleIds.length()").value(0));

        // Create a battle already assigned to the theater (battle write must cross-evict THEATERS).
        var battle = TestDataUtil.createTestBattleDtoA(conflict.getId());
        battle.setTheaterId(theaterId);
        battleService.create(battle);

        // The cached battleIds must now reflect the new battle, not the stale empty list.
        mockMvc.perform(get("/api/theaters/" + theaterId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.battleIds.length()").value(1));
    }
}
