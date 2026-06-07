package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.TreatyDto;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import com.betochimas.historical_conflicts_api.service.TreatyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TreatyControllerIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final TreatyService treatyService;
    private final ConflictService conflictService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TreatyControllerIntegrationTests(MockMvc mockMvc,
                                            TreatyService treatyService,
                                            ConflictService conflictService,
                                            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.treatyService = treatyService;
        this.conflictService = conflictService;
        this.objectMapper = objectMapper;
    }

    private ConflictDto savedConflict() {
        return conflictService.create(TestDataUtil.createTestConflictDtoA());
    }

    // --- POST ---

    @Test
    public void testThatCreateTreatyReturnsHttp201AndBody() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTreatyDtoA(conflict.getId()));

        mockMvc.perform(post("/api/treaties")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.name").value("Treaty of Versailles"))
                .andExpect(jsonPath("$.treatyType").value("PEACE"));
    }

    @Test
    public void testThatCreateTreatyWithoutConflictSucceeds() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTreatyDtoA(null));

        mockMvc.perform(post("/api/treaties")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conflictId").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.name").value("Treaty of Versailles"));
    }

    @Test
    public void testThatCreateTreatyReturnsHttp404WhenConflictMissing() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTreatyDtoA(999L));

        mockMvc.perform(post("/api/treaties")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Conflict not found: 999"));
    }

    @Test
    public void testThatCreateTreatyWithBlankNameReturnsHttp400() throws Exception {
        TreatyDto dto = TestDataUtil.createTestTreatyDtoA(null);
        dto.setName("");

        mockMvc.perform(post("/api/treaties")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: name is required"));
    }

    @Test
    public void testThatCreateTreatyWithNullTypeReturnsHttp400() throws Exception {
        TreatyDto dto = TestDataUtil.createTestTreatyDtoA(null);
        dto.setTreatyType(null);

        mockMvc.perform(post("/api/treaties")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- GET + filter ---

    @Test
    public void testThatListTreatiesReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/treaties")).andExpect(status().isOk());
    }

    @Test
    public void testThatListTreatiesFiltersByConflictId() throws Exception {
        ConflictDto conflictA = savedConflict();
        ConflictDto conflictB = conflictService.create(TestDataUtil.createTestConflictDtoB());
        treatyService.create(TestDataUtil.createTestTreatyDtoA(conflictA.getId()));
        treatyService.create(TestDataUtil.createTestTreatyDtoB(conflictB.getId()));

        mockMvc.perform(get("/api/treaties").param("conflictId", conflictA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Treaty of Versailles"));
    }

    @Test
    public void testThatGetTreatyReturnsHttp200WhenExists() throws Exception {
        TreatyDto saved = treatyService.create(TestDataUtil.createTestTreatyDtoA(null));

        mockMvc.perform(get("/api/treaties/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Treaty of Versailles"));
    }

    @Test
    public void testThatGetTreatyReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/treaties/{id}", 999)).andExpect(status().isNotFound());
    }

    // --- PUT / PATCH ---

    @Test
    public void testThatFullUpdateTreatyReturnsHttp404WhenNotExists() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTreatyDtoA(null));

        mockMvc.perform(put("/api/treaties/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatPartialUpdateTreatyOnlyUpdatesSuppliedFields() throws Exception {
        TreatyDto saved = treatyService.create(TestDataUtil.createTestTreatyDtoA(null));
        TreatyDto partial = TreatyDto.builder().location("Paris, France").build();

        mockMvc.perform(patch("/api/treaties/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Treaty of Versailles"))
                .andExpect(jsonPath("$.location").value("Paris, France"));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteTreatyReturnsHttp204AndIsGone() throws Exception {
        TreatyDto saved = treatyService.create(TestDataUtil.createTestTreatyDtoA(null));

        mockMvc.perform(delete("/api/treaties/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/treaties/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatCreateTreatyWithoutAuthReturnsHttp401() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTreatyDtoA(null));

        mockMvc.perform(post("/api/treaties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}
