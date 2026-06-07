package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.dto.TreatyDto;
import com.betochimas.historical_conflicts_api.domain.dto.TreatySignatoryDto;
import com.betochimas.historical_conflicts_api.service.NationService;
import com.betochimas.historical_conflicts_api.service.TreatyService;
import com.betochimas.historical_conflicts_api.service.TreatySignatoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TreatySignatoryControllerIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final TreatySignatoryService signatoryService;
    private final TreatyService treatyService;
    private final NationService nationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public TreatySignatoryControllerIntegrationTests(MockMvc mockMvc,
                                                     TreatySignatoryService signatoryService,
                                                     TreatyService treatyService,
                                                     NationService nationService,
                                                     ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.signatoryService = signatoryService;
        this.treatyService = treatyService;
        this.nationService = nationService;
        this.objectMapper = objectMapper;
    }

    private TreatyDto savedTreaty() {
        return treatyService.create(TestDataUtil.createTestTreatyDtoA(null));
    }

    private NationDto savedNation() {
        return nationService.create(TestDataUtil.createTestNationDtoA());
    }

    // --- POST ---

    @Test
    public void testThatCreateSignatoryReturnsHttp201AndBody() throws Exception {
        TreatyDto treaty = savedTreaty();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nation.getId()));

        mockMvc.perform(post("/api/treaty-signatories")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.treatyId").value(treaty.getId()))
                .andExpect(jsonPath("$.nationId").value(nation.getId()))
                .andExpect(jsonPath("$.role").value("SIGNATORY"));
    }

    @Test
    public void testThatCreateSignatoryReturnsHttp404WhenTreatyMissing() throws Exception {
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestTreatySignatoryDtoA(999L, nation.getId()));

        mockMvc.perform(post("/api/treaty-signatories")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Treaty not found: 999"));
    }

    @Test
    public void testThatCreateSignatoryReturnsHttp404WhenNationMissing() throws Exception {
        TreatyDto treaty = savedTreaty();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), 999L));

        mockMvc.perform(post("/api/treaty-signatories")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nation not found: 999"));
    }

    @Test
    public void testThatCreateSignatoryWithNullRoleReturnsHttp400() throws Exception {
        TreatyDto treaty = savedTreaty();
        NationDto nation = savedNation();
        TreatySignatoryDto dto = TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nation.getId());
        dto.setRole(null);

        mockMvc.perform(post("/api/treaty-signatories")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testThatDuplicateSignatoryReturnsHttp409() throws Exception {
        TreatyDto treaty = savedTreaty();
        NationDto nation = savedNation();
        signatoryService.create(TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nation.getId()));

        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nation.getId()));

        mockMvc.perform(post("/api/treaty-signatories")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    // --- GET + filters ---

    @Test
    public void testThatListSignatoriesFiltersByTreatyId() throws Exception {
        TreatyDto treatyA = savedTreaty();
        TreatyDto treatyB = treatyService.create(TestDataUtil.createTestTreatyDtoB(null));
        NationDto nation = savedNation();
        signatoryService.create(TestDataUtil.createTestTreatySignatoryDtoA(treatyA.getId(), nation.getId()));
        signatoryService.create(TestDataUtil.createTestTreatySignatoryDtoA(treatyB.getId(), nation.getId()));

        mockMvc.perform(get("/api/treaty-signatories").param("treatyId", treatyA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].treatyId").value(treatyA.getId()));
    }

    @Test
    public void testThatListSignatoriesFiltersByNationId() throws Exception {
        TreatyDto treaty = savedTreaty();
        NationDto nationA = savedNation();
        NationDto nationB = nationService.create(TestDataUtil.createTestNationDtoB());
        signatoryService.create(TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nationA.getId()));
        signatoryService.create(TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nationB.getId()));

        mockMvc.perform(get("/api/treaty-signatories").param("nationId", nationB.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nationId").value(nationB.getId()));
    }

    @Test
    public void testThatGetSignatoryReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/treaty-signatories/{id}", 999)).andExpect(status().isNotFound());
    }

    // --- PATCH / DELETE ---

    @Test
    public void testThatPartialUpdateSignatoryCanChangeRole() throws Exception {
        TreatyDto treaty = savedTreaty();
        NationDto nation = savedNation();
        TreatySignatoryDto saved = signatoryService.create(
                TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nation.getId()));
        TreatySignatoryDto partial = TreatySignatoryDto.builder()
                .role(com.betochimas.historical_conflicts_api.domain.model.SignatoryRole.WITNESS).build();

        mockMvc.perform(patch("/api/treaty-signatories/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.treatyId").value(treaty.getId()))
                .andExpect(jsonPath("$.role").value("WITNESS"));
    }

    @Test
    public void testThatDeleteSignatoryReturnsHttp204AndIsGone() throws Exception {
        TreatyDto treaty = savedTreaty();
        NationDto nation = savedNation();
        TreatySignatoryDto saved = signatoryService.create(
                TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nation.getId()));

        mockMvc.perform(delete("/api/treaty-signatories/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/treaty-signatories/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatCreateSignatoryWithoutAuthReturnsHttp401() throws Exception {
        TreatyDto treaty = savedTreaty();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestTreatySignatoryDtoA(treaty.getId(), nation.getId()));

        mockMvc.perform(post("/api/treaty-signatories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}
