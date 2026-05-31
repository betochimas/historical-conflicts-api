package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.service.BattleService;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BattleControllerIntegrationTests extends AbstractIntegrationTest {

    private final BattleService battleService;
    private final ConflictService conflictService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public BattleControllerIntegrationTests(
            MockMvc mockMvc,
            BattleService battleService,
            ConflictService conflictService,
            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.battleService = battleService;
        this.conflictService = conflictService;
        this.objectMapper = objectMapper;
    }

    private ConflictDto savedConflict() {
        return conflictService.create(TestDataUtil.createTestConflictDtoA());
    }

    // --- POST ---

    @Test
    public void testThatCreateBattleReturnsHttp201Created() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(post("/api/battles")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateBattleReturnsHttp404WhenConflictMissing() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestBattleDtoA(999L));

        mockMvc.perform(post("/api/battles")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Conflict not found: 999"));
    }

    @Test
    public void testThatCreateBattleReturnsSavedBattle() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(post("/api/battles")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.name").value("Battle of the Marne"))
                .andExpect(jsonPath("$.location").value("Marne River, France"))
                .andExpect(jsonPath("$.outcome").value("Allied victory"));
    }

    @Test
    public void testThatCreateBattleRoundTripsCoordinates() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(post("/api/battles")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.latitude").value(48.96))
                .andExpect(jsonPath("$.longitude").value(3.39));
    }

    // --- GET all ---

    @Test
    public void testThatListBattlesReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/battles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatListBattlesReturnsListOfBattles() throws Exception {
        ConflictDto conflict = savedConflict();
        battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(get("/api/battles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.content[0].name").value("Battle of the Marne"));
    }

    @Test
    public void testThatListBattlesFiltersByConflictId() throws Exception {
        ConflictDto conflictA = savedConflict();
        ConflictDto conflictB = conflictService.create(TestDataUtil.createTestConflictDtoB());
        battleService.create(TestDataUtil.createTestBattleDtoA(conflictA.getId()));
        battleService.create(TestDataUtil.createTestBattleDtoB(conflictB.getId()));

        mockMvc.perform(get("/api/battles")
                        .param("conflictId", conflictA.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Battle of the Marne"));
    }

    // --- GET by id ---

    @Test
    public void testThatGetBattleReturnsHttp200WhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(get("/api/battles/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatGetBattleReturnsBattleWhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(get("/api/battles/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.name").value("Battle of the Marne"))
                .andExpect(jsonPath("$.terrain").value("River valley"));
    }

    @Test
    public void testThatGetBattleReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/battles/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- PUT ---

    @Test
    public void testThatFullUpdateBattleReturnsHttp404WhenNotExists() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(put("/api/battles/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateBattleReturnsHttp200WhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestBattleDtoB(conflict.getId()));

        mockMvc.perform(put("/api/battles/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatFullUpdateBattleUpdatesExistingBattle() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));
        BattleDto updateDto = TestDataUtil.createTestBattleDtoB(conflict.getId());
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/battles/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Battle of Verdun"))
                .andExpect(jsonPath("$.location").value("Verdun, France"))
                .andExpect(jsonPath("$.outcome").value("French victory"));
    }

    // --- PATCH ---

    @Test
    public void testThatPartialUpdateBattleReturnsHttp404WhenNotExists() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(patch("/api/battles/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatPartialUpdateBattleReturnsHttp200WhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));
        BattleDto partialDto = BattleDto.builder().outcome("Decisive Allied victory").build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/battles/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatPartialUpdateBattleOnlyUpdatesSuppliedFields() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));
        BattleDto partialDto = BattleDto.builder().outcome("Decisive Allied victory").build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/battles/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Battle of the Marne"))
                .andExpect(jsonPath("$.location").value("Marne River, France"))
                .andExpect(jsonPath("$.outcome").value("Decisive Allied victory"));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteBattleReturnsHttp204() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(delete("/api/battles/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testThatDeleteBattleMeansBattleNoLongerExists() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto saved = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        mockMvc.perform(delete("/api/battles/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/battles/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
