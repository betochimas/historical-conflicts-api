package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import com.betochimas.historical_conflicts_api.service.BattleService;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import com.betochimas.historical_conflicts_api.service.TheaterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TheaterControllerIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final TheaterService theaterService;
    private final ConflictService conflictService;
    private final BattleService battleService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TheaterControllerIntegrationTests(
            MockMvc mockMvc,
            TheaterService theaterService,
            ConflictService conflictService,
            BattleService battleService,
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

    // --- POST ---

    @Test
    public void testThatCreateTheaterReturnsHttp201Created() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTheaterDtoA(conflict.getId()));

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateTheaterReturnsSavedTheater() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTheaterDtoA(conflict.getId()));

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.conflictId").value(conflict.getId()))
                .andExpect(jsonPath("$.name").value("Western Front"))
                .andExpect(jsonPath("$.region").value("France & Belgium"))
                .andExpect(jsonPath("$.outcome").value("Allied victory"));
    }

    @Test
    public void testThatCreateTheaterReturnsHttp404WhenConflictMissing() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTheaterDtoA(999L));

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Conflict not found: 999"));
    }

    @Test
    public void testThatCreateTheaterWithBlankNameReturnsHttp400() throws Exception {
        ConflictDto conflict = savedConflict();
        TheaterDto dto = TestDataUtil.createTestTheaterDtoA(conflict.getId());
        dto.setName("");
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name: name is required"));
    }

    @Test
    public void testThatCreateTheaterWithNullConflictIdReturnsHttp400() throws Exception {
        TheaterDto dto = TestDataUtil.createTestTheaterDtoA(null);
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // --- POST with battleIds (theater-side assignment) ---

    @Test
    public void testThatCreateTheaterWithBattleIdsAttachesThoseBattles() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto battle = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        TheaterDto dto = TestDataUtil.createTestTheaterDtoA(conflict.getId());
        dto.setBattleIds(List.of(battle.getId()));
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.battleIds.length()").value(1))
                .andExpect(jsonPath("$.battleIds[0]").value(battle.getId()));

        // The battle now reports the theater on its own representation.
        mockMvc.perform(get("/api/battles/{id}", battle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theaterId").isNumber());
    }

    @Test
    public void testThatCreateTheaterWithBattleFromDifferentConflictReturnsHttp400() throws Exception {
        ConflictDto conflictA = savedConflict();
        ConflictDto conflictB = conflictService.create(TestDataUtil.createTestConflictDtoB());
        BattleDto battleInB = battleService.create(TestDataUtil.createTestBattleDtoA(conflictB.getId()));

        TheaterDto dto = TestDataUtil.createTestTheaterDtoA(conflictA.getId());
        dto.setBattleIds(List.of(battleInB.getId()));
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testThatCreateTheaterWithMissingBattleIdReturnsHttp404() throws Exception {
        ConflictDto conflict = savedConflict();
        TheaterDto dto = TestDataUtil.createTestTheaterDtoA(conflict.getId());
        dto.setBattleIds(List.of(999L));
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/theaters")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Battle not found: 999"));
    }

    // --- GET ---

    @Test
    public void testThatListTheatersReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/theaters"))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatListTheatersReturnsListOfTheaters() throws Exception {
        ConflictDto conflict = savedConflict();
        theaterService.create(TestDataUtil.createTestTheaterDtoA(conflict.getId()));

        mockMvc.perform(get("/api/theaters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].name").value("Western Front"));
    }

    @Test
    public void testThatListTheatersFiltersByConflictId() throws Exception {
        ConflictDto conflictA = savedConflict();
        ConflictDto conflictB = conflictService.create(TestDataUtil.createTestConflictDtoB());
        theaterService.create(TestDataUtil.createTestTheaterDtoA(conflictA.getId()));
        theaterService.create(TestDataUtil.createTestTheaterDtoB(conflictB.getId()));

        mockMvc.perform(get("/api/theaters").param("conflictId", conflictA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Western Front"));
    }

    @Test
    public void testThatGetTheaterReturnsHttp200AndBodyWhenExists() throws Exception {
        ConflictDto conflict = savedConflict();
        TheaterDto saved = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflict.getId()));

        mockMvc.perform(get("/api/theaters/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Western Front"))
                .andExpect(jsonPath("$.battleIds").isArray());
    }

    @Test
    public void testThatGetTheaterReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/theaters/{id}", 999))
                .andExpect(status().isNotFound());
    }

    // --- PUT / PATCH ---

    @Test
    public void testThatFullUpdateTheaterReturnsHttp404WhenNotExists() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTheaterDtoA(conflict.getId()));

        mockMvc.perform(put("/api/theaters/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateTheaterUpdatesExistingTheater() throws Exception {
        ConflictDto conflict = savedConflict();
        TheaterDto saved = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflict.getId()));
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTheaterDtoB(conflict.getId()));

        mockMvc.perform(put("/api/theaters/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Eastern Front"))
                .andExpect(jsonPath("$.outcome").value("Central Powers victory"));
    }

    @Test
    public void testThatPartialUpdateTheaterOnlyUpdatesSuppliedFields() throws Exception {
        ConflictDto conflict = savedConflict();
        TheaterDto saved = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflict.getId()));
        TheaterDto partial = TheaterDto.builder().outcome("Stalemate").build();
        String json = objectMapper.writeValueAsString(partial);

        mockMvc.perform(patch("/api/theaters/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Western Front"))
                .andExpect(jsonPath("$.outcome").value("Stalemate"));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteTheaterReturnsHttp204AndIsGone() throws Exception {
        ConflictDto conflict = savedConflict();
        TheaterDto saved = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflict.getId()));

        mockMvc.perform(delete("/api/theaters/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/theaters/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatDeleteTheaterWithBattlesReturnsHttp409() throws Exception {
        ConflictDto conflict = savedConflict();
        BattleDto battle = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));
        TheaterDto dto = TestDataUtil.createTestTheaterDtoA(conflict.getId());
        dto.setBattleIds(List.of(battle.getId()));
        TheaterDto saved = theaterService.create(dto);

        mockMvc.perform(delete("/api/theaters/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isConflict());
    }

    // --- Battle-side assignment + filter ---

    @Test
    public void testThatBattleCanBeAssignedToTheaterViaBattlePatchAndFilters() throws Exception {
        ConflictDto conflict = savedConflict();
        TheaterDto theater = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflict.getId()));
        BattleDto battle = battleService.create(TestDataUtil.createTestBattleDtoA(conflict.getId()));

        BattleDto patch = BattleDto.builder().theaterId(theater.getId()).build();
        mockMvc.perform(patch("/api/battles/{id}", battle.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theaterId").value(theater.getId()));

        mockMvc.perform(get("/api/battles").param("theaterId", theater.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(battle.getId()));
    }

    @Test
    public void testThatAssigningBattleToTheaterOfDifferentConflictReturnsHttp400() throws Exception {
        ConflictDto conflictA = savedConflict();
        ConflictDto conflictB = conflictService.create(TestDataUtil.createTestConflictDtoB());
        TheaterDto theaterInA = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflictA.getId()));
        BattleDto battleInB = battleService.create(TestDataUtil.createTestBattleDtoA(conflictB.getId()));

        BattleDto patch = BattleDto.builder().theaterId(theaterInA.getId()).build();
        mockMvc.perform(patch("/api/battles/{id}", battleInB.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isBadRequest());
    }

    // --- Auth ---

    @Test
    public void testThatCreateTheaterWithoutAuthReturnsHttp401() throws Exception {
        ConflictDto conflict = savedConflict();
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestTheaterDtoA(conflict.getId()));

        mockMvc.perform(post("/api/theaters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}
