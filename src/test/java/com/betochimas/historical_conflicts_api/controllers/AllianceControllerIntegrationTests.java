package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.AllianceDto;
import com.betochimas.historical_conflicts_api.domain.model.AllianceType;
import com.betochimas.historical_conflicts_api.service.AllianceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AllianceControllerIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final AllianceService allianceService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AllianceControllerIntegrationTests(MockMvc mockMvc,
                                              AllianceService allianceService,
                                              ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.allianceService = allianceService;
        this.objectMapper = objectMapper;
    }

    // --- POST ---

    @Test
    public void testThatCreateAllianceReturnsHttp201AndBody() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestAllianceDtoA());

        mockMvc.perform(post("/api/alliances")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Triple Entente"))
                .andExpect(jsonPath("$.allianceType").value("MILITARY"));
    }

    @Test
    public void testThatCreateAllianceWithBlankNameReturnsHttp400() throws Exception {
        AllianceDto dto = TestDataUtil.createTestAllianceDtoA();
        dto.setName("");

        mockMvc.perform(post("/api/alliances")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: name is required"));
    }

    @Test
    public void testThatCreateAllianceWithNullTypeReturnsHttp400() throws Exception {
        AllianceDto dto = TestDataUtil.createTestAllianceDtoA();
        dto.setAllianceType(null);

        mockMvc.perform(post("/api/alliances")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testThatCreateAllianceWithoutAuthReturnsHttp401() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestAllianceDtoA());

        mockMvc.perform(post("/api/alliances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    // --- GET ---

    @Test
    public void testThatListAlliancesReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/alliances")).andExpect(status().isOk());
    }

    @Test
    public void testThatListAlliancesReturnsContent() throws Exception {
        allianceService.create(TestDataUtil.createTestAllianceDtoA());

        mockMvc.perform(get("/api/alliances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Triple Entente"));
    }

    @Test
    public void testThatGetAllianceReturnsHttp200WhenExists() throws Exception {
        AllianceDto saved = allianceService.create(TestDataUtil.createTestAllianceDtoA());

        mockMvc.perform(get("/api/alliances/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Triple Entente"));
    }

    @Test
    public void testThatGetAllianceReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/alliances/{id}", 999)).andExpect(status().isNotFound());
    }

    // --- PUT / PATCH ---

    @Test
    public void testThatFullUpdateAllianceReturnsHttp404WhenNotExists() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestAllianceDtoA());

        mockMvc.perform(put("/api/alliances/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateAllianceUpdatesExisting() throws Exception {
        AllianceDto saved = allianceService.create(TestDataUtil.createTestAllianceDtoA());
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestAllianceDtoB());

        mockMvc.perform(put("/api/alliances/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Central Powers"))
                .andExpect(jsonPath("$.allianceType").value("COALITION"));
    }

    @Test
    public void testThatPartialUpdateAllianceOnlyUpdatesSuppliedFields() throws Exception {
        AllianceDto saved = allianceService.create(TestDataUtil.createTestAllianceDtoA());
        AllianceDto partial = AllianceDto.builder().allianceType(AllianceType.DEFENSIVE).build();

        mockMvc.perform(patch("/api/alliances/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Triple Entente"))
                .andExpect(jsonPath("$.allianceType").value("DEFENSIVE"));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteAllianceReturnsHttp204AndIsGone() throws Exception {
        AllianceDto saved = allianceService.create(TestDataUtil.createTestAllianceDtoA());

        mockMvc.perform(delete("/api/alliances/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/alliances/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
