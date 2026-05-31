package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.model.LeaderRole;
import com.betochimas.historical_conflicts_api.service.LeaderService;
import com.betochimas.historical_conflicts_api.service.NationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LeaderControllerIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final LeaderService leaderService;
    private final NationService nationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public LeaderControllerIntegrationTests(
            MockMvc mockMvc,
            LeaderService leaderService,
            NationService nationService,
            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.leaderService = leaderService;
        this.nationService = nationService;
        this.objectMapper = objectMapper;
    }

    private NationDto savedNation() {
        return nationService.create(TestDataUtil.createTestNationDtoA());
    }

    // --- POST ---

    @Test
    public void testThatCreateLeaderReturnsHttp201Created() throws Exception {
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(post("/api/leaders")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateLeaderReturnsSavedLeader() throws Exception {
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(post("/api/leaders")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nationId").value(nation.getId()))
                .andExpect(jsonPath("$.name").value("Woodrow Wilson"))
                .andExpect(jsonPath("$.role").value("HEAD_OF_STATE"))
                .andExpect(jsonPath("$.title").value("President of the United States"));
    }

    @Test
    public void testThatCreateLeaderReturnsHttp404WhenNationMissing() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestLeaderDtoA(999L));

        mockMvc.perform(post("/api/leaders")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nation not found: 999"));
    }

    @Test
    public void testThatCreateLeaderWithBlankNameReturnsHttp400() throws Exception {
        NationDto nation = savedNation();
        LeaderDto dto = TestDataUtil.createTestLeaderDtoA(nation.getId());
        dto.setName("");
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/leaders")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: name is required"));
    }

    @Test
    public void testThatCreateLeaderWithNullNationIdReturnsHttp400() throws Exception {
        LeaderDto dto = TestDataUtil.createTestLeaderDtoA(null);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/leaders")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testThatCreateLeaderWithNullRoleReturnsHttp400() throws Exception {
        NationDto nation = savedNation();
        LeaderDto dto = TestDataUtil.createTestLeaderDtoA(nation.getId());
        dto.setRole(null);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/leaders")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // --- GET ---

    @Test
    public void testThatListLeadersReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/leaders"))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatListLeadersReturnsListOfLeaders() throws Exception {
        NationDto nation = savedNation();
        leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(get("/api/leaders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].name").value("Woodrow Wilson"));
    }

    @Test
    public void testThatListLeadersFiltersByNationId() throws Exception {
        NationDto nationA = savedNation();
        NationDto nationB = nationService.create(TestDataUtil.createTestNationDtoB());
        leaderService.create(TestDataUtil.createTestLeaderDtoA(nationA.getId()));
        leaderService.create(TestDataUtil.createTestLeaderDtoB(nationB.getId()));

        mockMvc.perform(get("/api/leaders").param("nationId", nationA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Woodrow Wilson"));
    }

    @Test
    public void testThatGetLeaderReturnsHttp200AndBodyWhenExists() throws Exception {
        NationDto nation = savedNation();
        LeaderDto saved = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(get("/api/leaders/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Woodrow Wilson"))
                .andExpect(jsonPath("$.role").value("HEAD_OF_STATE"));
    }

    @Test
    public void testThatGetLeaderReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/leaders/{id}", 999))
                .andExpect(status().isNotFound());
    }

    // --- PUT / PATCH ---

    @Test
    public void testThatFullUpdateLeaderReturnsHttp404WhenNotExists() throws Exception {
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(put("/api/leaders/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateLeaderUpdatesExistingLeader() throws Exception {
        NationDto nation = savedNation();
        LeaderDto saved = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestLeaderDtoB(nation.getId()));

        mockMvc.perform(put("/api/leaders/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Ferdinand Foch"))
                .andExpect(jsonPath("$.role").value("GENERAL"));
    }

    @Test
    public void testThatPartialUpdateLeaderOnlyUpdatesSuppliedFields() throws Exception {
        NationDto nation = savedNation();
        LeaderDto saved = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));
        LeaderDto partial = LeaderDto.builder().title("Former President").build();
        String json = objectMapper.writeValueAsString(partial);

        mockMvc.perform(patch("/api/leaders/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Woodrow Wilson"))
                .andExpect(jsonPath("$.title").value("Former President"))
                .andExpect(jsonPath("$.role").value("HEAD_OF_STATE"));
    }

    @Test
    public void testThatPartialUpdateLeaderCanChangeRole() throws Exception {
        NationDto nation = savedNation();
        LeaderDto saved = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));
        LeaderDto partial = LeaderDto.builder().role(LeaderRole.DIPLOMAT).build();
        String json = objectMapper.writeValueAsString(partial);

        mockMvc.perform(patch("/api/leaders/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("DIPLOMAT"));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteLeaderReturnsHttp204AndIsGone() throws Exception {
        NationDto nation = savedNation();
        LeaderDto saved = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(delete("/api/leaders/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/leaders/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    // --- Auth ---

    @Test
    public void testThatCreateLeaderWithoutAuthReturnsHttp401() throws Exception {
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(post("/api/leaders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testThatDeleteLeaderWithoutAuthReturnsHttp401() throws Exception {
        NationDto nation = savedNation();
        LeaderDto saved = leaderService.create(TestDataUtil.createTestLeaderDtoA(nation.getId()));

        mockMvc.perform(delete("/api/leaders/{id}", saved.getId()))
                .andExpect(status().isUnauthorized());
    }
}
