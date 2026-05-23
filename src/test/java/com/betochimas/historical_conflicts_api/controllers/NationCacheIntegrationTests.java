package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.repository.NationRepository;
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
 * Verifies the Redis cache behaviour on the Nation read path: a cache hit skips the repository,
 * and a write evicts the entry so the next read goes back to the database.
 */
public class NationCacheIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final NationService nationService;
    private final ObjectMapper objectMapper;

    @MockitoSpyBean
    private NationRepository nationRepository;

    @Autowired
    public NationCacheIntegrationTests(MockMvc mockMvc, NationService nationService, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.nationService = nationService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testThatSecondGetIsServedFromCacheAndDoesNotHitRepository() throws Exception {
        Long id = nationService.create(TestDataUtil.createTestNationDtoA()).getId();

        // First read: cache miss → repository is queried and the result is cached.
        mockMvc.perform(get("/api/nations/" + id)).andExpect(status().isOk());
        // Second read: cache hit → repository must NOT be queried again.
        mockMvc.perform(get("/api/nations/" + id)).andExpect(status().isOk());

        verify(nationRepository, times(1)).findById(id);
    }

    @Test
    public void testThatUpdateEvictsCacheSoNextGetHitsRepositoryAgain() throws Exception {
        NationDto created = nationService.create(TestDataUtil.createTestNationDtoA());
        Long id = created.getId();

        mockMvc.perform(get("/api/nations/" + id)).andExpect(status().isOk());           // miss → findById #1

        created.setRegion("Mediterranean");
        mockMvc.perform(put("/api/nations/" + id)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk());                                              // evicts the entry

        mockMvc.perform(get("/api/nations/" + id)).andExpect(status().isOk());           // miss again → findById #2

        verify(nationRepository, times(2)).findById(id);
    }
}
