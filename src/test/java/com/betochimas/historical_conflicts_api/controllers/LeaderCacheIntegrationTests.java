package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.repository.LeaderRepository;
import com.betochimas.historical_conflicts_api.service.LeaderService;
import com.betochimas.historical_conflicts_api.service.NationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies the cache behaviour on the Leader read path: a cache hit skips the repository,
 * and a write evicts the entry so the next read goes back to the database.
 */
public class LeaderCacheIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final LeaderService leaderService;
    private final NationService nationService;
    private final ObjectMapper objectMapper;

    @MockitoSpyBean
    private LeaderRepository leaderRepository;

    @Autowired
    public LeaderCacheIntegrationTests(MockMvc mockMvc,
                                       LeaderService leaderService,
                                       NationService nationService,
                                       ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.leaderService = leaderService;
        this.nationService = nationService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatSecondGetIsServedFromCacheAndDoesNotHitRepository() throws Exception {
        NationDto nation = nationService.create(TestDataUtil.createTestNationDtoA());
        Long id = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId())).getId();

        mockMvc.perform(get("/api/leaders/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/leaders/" + id)).andExpect(status().isOk());

        verify(leaderRepository, times(1)).findById(id);
    }

    @Test
    public void testThatUpdateEvictsCacheSoNextGetHitsRepositoryAgain() throws Exception {
        NationDto nation = nationService.create(TestDataUtil.createTestNationDtoA());
        LeaderDto created = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));
        Long id = created.getId();

        mockMvc.perform(get("/api/leaders/" + id)).andExpect(status().isOk());           // miss → findById #1

        created.setTitle("Former President");
        mockMvc.perform(put("/api/leaders/" + id)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk());                                              // evicts the entry

        mockMvc.perform(get("/api/leaders/" + id)).andExpect(status().isOk());           // miss again → findById #2

        verify(leaderRepository, times(2)).findById(id);
    }
}
