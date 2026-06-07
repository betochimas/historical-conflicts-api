package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.AllianceDto;
import com.betochimas.historical_conflicts_api.repository.AllianceRepository;
import com.betochimas.historical_conflicts_api.service.AllianceService;
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
 * Verifies the cache behaviour on the Alliance read path: a cache hit skips the repository,
 * and a write evicts the entry so the next read goes back to the database.
 */
public class AllianceCacheIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final AllianceService allianceService;
    private final ObjectMapper objectMapper;

    @MockitoSpyBean
    private AllianceRepository allianceRepository;

    @Autowired
    public AllianceCacheIntegrationTests(MockMvc mockMvc,
                                         AllianceService allianceService,
                                         ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.allianceService = allianceService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatSecondGetIsServedFromCacheAndDoesNotHitRepository() throws Exception {
        Long id = allianceService.create(TestDataUtil.createTestAllianceDtoA()).getId();

        mockMvc.perform(get("/api/alliances/" + id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/alliances/" + id)).andExpect(status().isOk());

        verify(allianceRepository, times(1)).findById(id);
    }

    @Test
    public void testThatUpdateEvictsCacheSoNextGetHitsRepositoryAgain() throws Exception {
        AllianceDto created = allianceService.create(TestDataUtil.createTestAllianceDtoA());
        Long id = created.getId();

        mockMvc.perform(get("/api/alliances/" + id)).andExpect(status().isOk());           // miss → findById #1

        created.setDescription("Updated description");
        mockMvc.perform(put("/api/alliances/" + id)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk());                                                // evicts the entry

        mockMvc.perform(get("/api/alliances/" + id)).andExpect(status().isOk());           // miss again → findById #2

        verify(allianceRepository, times(2)).findById(id);
    }
}
