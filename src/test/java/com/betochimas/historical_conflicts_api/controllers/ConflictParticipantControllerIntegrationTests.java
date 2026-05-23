package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.model.ParticipantRole;
import com.betochimas.historical_conflicts_api.service.ConflictParticipantService;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import com.betochimas.historical_conflicts_api.service.NationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConflictParticipantControllerIntegrationTests extends AbstractIntegrationTest {

    private final ConflictParticipantService participantService;
    private final ConflictService conflictService;
    private final NationService nationService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ConflictParticipantControllerIntegrationTests(
            MockMvc mockMvc,
            ConflictParticipantService participantService,
            ConflictService conflictService,
            NationService nationService,
            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.participantService = participantService;
        this.conflictService = conflictService;
        this.nationService = nationService;
        this.objectMapper = objectMapper;
    }

    private ConflictDto savedConflict() {
        return conflictService.create(TestDataUtil.createTestConflictDtoA());
    }

    private NationDto savedNation() {
        return nationService.create(TestDataUtil.createTestNationDtoA());
    }

    private ConflictParticipantDto savedParticipant(Long conflictId, Long nationId) {
        return participantService.create(
                TestDataUtil.createTestConflictParticipantDtoA(conflictId, nationId));
    }

    // --- POST ---

    @Test
    public void testThatCreateParticipantReturnsHttp201Created() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestConflictParticipantDtoA(conflict.getId(), nation.getId()));

        mockMvc.perform(post("/api/conflict-participants")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateParticipantReturnsHttp404WhenConflictMissing() throws Exception {
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestConflictParticipantDtoA(999L, nation.getId()));

        mockMvc.perform(post("/api/conflict-participants")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Conflict not found: 999"));
    }

    @Test
    public void testThatCreateParticipantReturnsHttp404WhenNationMissing() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestConflictParticipantDtoA(conflict.getId(), 999L));

        mockMvc.perform(post("/api/conflict-participants")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nation not found: 999"));
    }

    @Test
    public void testThatCreateParticipantReturnsSavedParticipant() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestConflictParticipantDtoA(conflict.getId(), nation.getId()));

        mockMvc.perform(post("/api/conflict-participants")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.nationId").value(nation.getId()))
                .andExpect(jsonPath("$.role").value("ATTACKER"))
                .andExpect(jsonPath("$.troopsCommitted").value(4_000_000))
                .andExpect(jsonPath("$.outcome").value("Victory"));
    }

    // --- GET all ---

    @Test
    public void testThatListParticipantsReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/conflict-participants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatListParticipantsReturnsListOfParticipants() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        savedParticipant(conflict.getId(), nation.getId());

        mockMvc.perform(get("/api/conflict-participants")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.content[0].nationId").value(nation.getId()))
                .andExpect(jsonPath("$.content[0].role").value("ATTACKER"));
    }

    @Test
    public void testThatListParticipantsFiltersByConflictId() throws Exception {
        ConflictDto conflictA = savedConflict();
        ConflictDto conflictB = conflictService.create(TestDataUtil.createTestConflictDtoB());
        NationDto nationA = nationService.create(TestDataUtil.createTestNationDtoA());
        NationDto nationB = nationService.create(TestDataUtil.createTestNationDtoB());
        savedParticipant(conflictA.getId(), nationA.getId());
        participantService.create(
                TestDataUtil.createTestConflictParticipantDtoB(conflictB.getId(), nationB.getId()));

        mockMvc.perform(get("/api/conflict-participants")
                        .param("conflictId", conflictA.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].conflictId").value(conflictA.getId()));
    }

    // --- GET by id ---

    @Test
    public void testThatGetParticipantReturnsHttp200WhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());

        mockMvc.perform(get("/api/conflict-participants/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatGetParticipantReturnsParticipantWhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());

        mockMvc.perform(get("/api/conflict-participants/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.nationId").value(nation.getId()))
                .andExpect(jsonPath("$.role").value("ATTACKER"))
                .andExpect(jsonPath("$.casualties").value(1_800_000));
    }

    @Test
    public void testThatGetParticipantReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/conflict-participants/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- PUT ---

    @Test
    public void testThatFullUpdateParticipantReturnsHttp404WhenNotExists() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestConflictParticipantDtoA(conflict.getId(), nation.getId()));

        mockMvc.perform(put("/api/conflict-participants/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateParticipantReturnsHttp200WhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestConflictParticipantDtoB(conflict.getId(), nation.getId()));

        mockMvc.perform(put("/api/conflict-participants/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatFullUpdateParticipantUpdatesExistingParticipant() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());
        ConflictParticipantDto updateDto =
                TestDataUtil.createTestConflictParticipantDtoB(conflict.getId(), nation.getId());
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/conflict-participants/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.role").value("DEFENDER"))
                .andExpect(jsonPath("$.troopsCommitted").value(3_500_000))
                .andExpect(jsonPath("$.outcome").value("Defeat"));
    }

    // --- PATCH ---

    @Test
    public void testThatPartialUpdateParticipantReturnsHttp404WhenNotExists() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestConflictParticipantDtoA(conflict.getId(), nation.getId()));

        mockMvc.perform(patch("/api/conflict-participants/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatPartialUpdateParticipantReturnsHttp200WhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());
        ConflictParticipantDto partialDto = ConflictParticipantDto.builder()
                .casualties(2_000_000).build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/conflict-participants/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatPartialUpdateParticipantOnlyUpdatesSuppliedFields() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());
        ConflictParticipantDto partialDto = ConflictParticipantDto.builder()
                .role(ParticipantRole.COALITION)
                .casualties(2_000_000)
                .build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/conflict-participants/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.role").value("COALITION"))
                .andExpect(jsonPath("$.casualties").value(2_000_000))
                .andExpect(jsonPath("$.troopsCommitted").value(4_000_000))
                .andExpect(jsonPath("$.outcome").value("Victory"));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteParticipantReturnsHttp204() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());

        mockMvc.perform(delete("/api/conflict-participants/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testThatDeleteParticipantMeansParticipantNoLongerExists() throws Exception {
        ConflictDto conflict = savedConflict();
        NationDto nation = savedNation();
        ConflictParticipantDto saved = savedParticipant(conflict.getId(), nation.getId());

        mockMvc.perform(delete("/api/conflict-participants/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/conflict-participants/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
