package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.service.NationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NationControllerIntegrationTests extends AbstractIntegrationTest {

    private final NationService nationService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public NationControllerIntegrationTests(MockMvc mockMvc, NationService nationService, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.nationService = nationService;
        this.objectMapper = objectMapper;
    }

    // --- POST ---

    @Test
    public void testThatCreateNationReturnsHttp201Created() throws Exception {
        NationDto nationDto = TestDataUtil.createTestNationDtoA();
        String json = objectMapper.writeValueAsString(nationDto);

        mockMvc.perform(post("/api/nations")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatCreateNationReturnsSavedNation() throws Exception {
        NationDto nationDto = TestDataUtil.createTestNationDtoA();
        String json = objectMapper.writeValueAsString(nationDto);

        mockMvc.perform(post("/api/nations")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Roman Empire"))
                .andExpect(jsonPath("$.region").value("Europe"))
                .andExpect(jsonPath("$.foundedYear").value(-27))
                .andExpect(jsonPath("$.dissolvedYear").value(476));
    }

    // --- GET all ---

    @Test
    public void testThatListNationsReturnsHttp200() throws Exception {
        mockMvc.perform(get("/api/nations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatListNationsReturnsListOfNations() throws Exception {
        nationService.create(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(get("/api/nations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").isNumber())
                .andExpect(jsonPath("$.content[0].name").value("Roman Empire"))
                .andExpect(jsonPath("$.content[0].region").value("Europe"));
    }

    // --- GET by id ---

    @Test
    public void testThatGetNationReturnsHttp200WhenExists() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(get("/api/nations/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatGetNationReturnsNationWhenExists() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(get("/api/nations/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Roman Empire"))
                .andExpect(jsonPath("$.region").value("Europe"));
    }

    @Test
    public void testThatGetNationReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/nations/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // --- PUT ---

    @Test
    public void testThatFullUpdateNationReturnsHttp404WhenNotExists() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(put("/api/nations/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatFullUpdateNationReturnsHttp200WhenExists() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestNationDtoB());

        mockMvc.perform(put("/api/nations/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatFullUpdateNationUpdatesExistingNation() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());
        NationDto updateDto = TestDataUtil.createTestNationDtoB();
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/nations/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Ottoman Empire"))
                .andExpect(jsonPath("$.region").value("Middle East & Europe"))
                .andExpect(jsonPath("$.foundedYear").value(1299));
    }

    // --- PATCH ---

    @Test
    public void testThatPartialUpdateNationReturnsHttp404WhenNotExists() throws Exception {
        String json = objectMapper.writeValueAsString(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(patch("/api/nations/{id}", 999)
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatPartialUpdateNationReturnsHttp200WhenExists() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());
        NationDto partialDto = NationDto.builder().name("Eastern Roman Empire").build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/nations/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatPartialUpdateNationOnlyUpdatesSuppliedFields() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());
        NationDto partialDto = NationDto.builder().name("Eastern Roman Empire").build();
        String json = objectMapper.writeValueAsString(partialDto);

        mockMvc.perform(patch("/api/nations/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Eastern Roman Empire"))
                .andExpect(jsonPath("$.region").value("Europe"))
                .andExpect(jsonPath("$.foundedYear").value(-27));
    }

    // --- DELETE ---

    @Test
    public void testThatDeleteNationReturnsHttp204() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(delete("/api/nations/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testThatDeleteNationMeansNationNoLongerExists() throws Exception {
        NationDto saved = nationService.create(TestDataUtil.createTestNationDtoA());

        mockMvc.perform(delete("/api/nations/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/nations/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
