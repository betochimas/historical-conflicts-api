package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.model.ConflictType;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConflictControllerIntegrationTests extends AbstractIntegrationTest {

    private final ConflictService conflictService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ConflictControllerIntegrationTests(MockMvc mockMvc, ConflictService conflictService, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.conflictService = conflictService;
        this.objectMapper = objectMapper;
    }

    // --- POST ---

    @Test
    public void testThatCreateConflictReturnsHttp201Created() throws Exception {
        ConflictDto conflictDto = TestDataUtil.createTestConflictDtoA();
        String json = objectMapper.writeValueAsString(conflictDto);

        mockMvc.perform(post("/api/conflicts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateConflictReturnsSavedConflict() throws Exception {
        ConflictDto conflictDto = TestDataUtil.createTestConflictDtoA();
        String json = objectMapper.writeValueAsString(conflictDto);

        mockMvc.perform(post("/api/conflicts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("World War I"))
                .andExpect(jsonPath("$.conflictType").value("WAR"))
                .andExpect(jsonPath("$.startDate").value("1914-07-28"))
                .andExpect(jsonPath("$.outcome").value("Allied victory"));
    }

    // --- GET all ---

    @Test
    public void testThatListConflictsReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/conflicts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatListConflictsReturnsListOfConflicts() throws Exception {
        conflictService.create(TestDataUtil.createTestConflictDtoA());

        mockMvc.perform(get("/api/conflicts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value("World War I"))
                .andExpect(jsonPath("$[0].conflictType").value("WAR"));
    }

    // --- GET by id ---

    @Test
    public void testThatGetConflictReturnsHttp200WhenExists() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());

        mockMvc.perform(get("/api/conflicts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatGetConflictReturnsConflictWhenExists() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());

        mockMvc.perform(get("/api/conflicts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("World War I"))
                .andExpect(jsonPath("$.conflictType").value("WAR"))
                .andExpect(jsonPath("$.endDate").value("1918-11-11"));
    }

    @Test
    public void testThatGetConflictReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/conflicts/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- PUT ---

    @Test
    public void testThatFullUpdateConflictReturnsHttp404WhenNotExists() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestConflictDtoA());

        mockMvc.perform(put("/api/conflicts/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateConflictReturnsHttp200WhenExists() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestConflictDtoB());

        mockMvc.perform(put("/api/conflicts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatFullUpdateConflictUpdatesExistingConflict() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());
        ConflictDto updateDto = TestDataUtil.createTestConflictDtoB();
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/conflicts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Hundred Years War"))
                .andExpect(jsonPath("$.conflictType").value("WAR"))
                .andExpect(jsonPath("$.outcome").value("French victory"));
    }

    // --- PATCH ---

    @Test
    public void testThatPartialUpdateConflictReturnsHttp404WhenNotExists() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestConflictDtoA());

        mockMvc.perform(patch("/api/conflicts/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatPartialUpdateConflictReturnsHttp200WhenExists() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());
        ConflictDto partialDto = ConflictDto.builder().outcome("Pyrrhic victory").build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/conflicts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatPartialUpdateConflictOnlyUpdatesSuppliedFields() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());
        ConflictDto partialDto = ConflictDto.builder()
                .outcome("Pyrrhic victory")
                .conflictType(ConflictType.PROXY_WAR)
                .build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/conflicts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("World War I"))
                .andExpect(jsonPath("$.conflictType").value("PROXY_WAR"))
                .andExpect(jsonPath("$.outcome").value("Pyrrhic victory"))
                .andExpect(jsonPath("$.startDate").value("1914-07-28"));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteConflictReturnsHttp204() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());

        mockMvc.perform(delete("/api/conflicts/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testThatDeleteConflictMeansConflictNoLongerExists() throws Exception {
        ConflictDto saved = conflictService.create(TestDataUtil.createTestConflictDtoA());

        mockMvc.perform(delete("/api/conflicts/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/conflicts/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
