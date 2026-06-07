package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.AllianceDto;
import com.betochimas.historical_conflicts_api.domain.dto.AllianceMemberDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.service.AllianceMemberService;
import com.betochimas.historical_conflicts_api.service.AllianceService;
import com.betochimas.historical_conflicts_api.service.NationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AllianceMemberControllerIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final AllianceMemberService memberService;
    private final AllianceService allianceService;
    private final NationService nationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AllianceMemberControllerIntegrationTests(MockMvc mockMvc,
                                                    AllianceMemberService memberService,
                                                    AllianceService allianceService,
                                                    NationService nationService,
                                                    ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.memberService = memberService;
        this.allianceService = allianceService;
        this.nationService = nationService;
        this.objectMapper = objectMapper;
    }

    private AllianceDto savedAlliance() {
        return allianceService.create(TestDataUtil.createTestAllianceDtoA());
    }

    private NationDto savedNation() {
        return nationService.create(TestDataUtil.createTestNationDtoA());
    }

    // --- POST ---

    @Test
    public void testThatCreateMemberReturnsHttp201AndBody() throws Exception {
        AllianceDto alliance = savedAlliance();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nation.getId()));

        mockMvc.perform(post("/api/alliance-members")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.allianceId").value(alliance.getId()))
                .andExpect(jsonPath("$.nationId").value(nation.getId()));
    }

    @Test
    public void testThatCreateMemberReturnsHttp404WhenAllianceMissing() throws Exception {
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestAllianceMemberDtoA(999L, nation.getId()));

        mockMvc.perform(post("/api/alliance-members")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Alliance not found: 999"));
    }

    @Test
    public void testThatCreateMemberReturnsHttp404WhenNationMissing() throws Exception {
        AllianceDto alliance = savedAlliance();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), 999L));

        mockMvc.perform(post("/api/alliance-members")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nation not found: 999"));
    }

    @Test
    public void testThatCreateMemberWithNullAllianceIdReturnsHttp400() throws Exception {
        NationDto nation = savedNation();
        AllianceMemberDto dto = TestDataUtil.createTestAllianceMemberDtoA(null, nation.getId());

        mockMvc.perform(post("/api/alliance-members")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testThatDuplicateMembershipReturnsHttp409() throws Exception {
        AllianceDto alliance = savedAlliance();
        NationDto nation = savedNation();
        memberService.create(TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nation.getId()));

        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nation.getId()));

        mockMvc.perform(post("/api/alliance-members")
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    // --- GET + filters ---

    @Test
    public void testThatListMembersFiltersByAllianceId() throws Exception {
        AllianceDto allianceA = savedAlliance();
        AllianceDto allianceB = allianceService.create(TestDataUtil.createTestAllianceDtoB());
        NationDto nation = savedNation();
        memberService.create(TestDataUtil.createTestAllianceMemberDtoA(allianceA.getId(), nation.getId()));
        memberService.create(TestDataUtil.createTestAllianceMemberDtoA(allianceB.getId(), nation.getId()));

        mockMvc.perform(get("/api/alliance-members").param("allianceId", allianceA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].allianceId").value(allianceA.getId()));
    }

    @Test
    public void testThatListMembersFiltersByNationId() throws Exception {
        AllianceDto alliance = savedAlliance();
        NationDto nationA = savedNation();
        NationDto nationB = nationService.create(TestDataUtil.createTestNationDtoB());
        memberService.create(TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nationA.getId()));
        memberService.create(TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nationB.getId()));

        mockMvc.perform(get("/api/alliance-members").param("nationId", nationB.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nationId").value(nationB.getId()));
    }

    @Test
    public void testThatGetMemberReturnsHttp404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/alliance-members/{id}", 999)).andExpect(status().isNotFound());
    }

    // --- PATCH / DELETE ---

    @Test
    public void testThatPartialUpdateMemberUpdatesLeftDate() throws Exception {
        AllianceDto alliance = savedAlliance();
        NationDto nation = savedNation();
        AllianceMemberDto saved = memberService.create(
                TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nation.getId()));
        AllianceMemberDto partial = AllianceMemberDto.builder()
                .leftDate(java.time.LocalDate.of(1917, 11, 7)).build();

        mockMvc.perform(patch("/api/alliance-members/{id}", saved.getId())
                        .header("Authorization", authHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partial)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allianceId").value(alliance.getId()))
                .andExpect(jsonPath("$.leftDate").value("1917-11-07"));
    }

    @Test
    public void testThatDeleteMemberReturnsHttp204AndIsGone() throws Exception {
        AllianceDto alliance = savedAlliance();
        NationDto nation = savedNation();
        AllianceMemberDto saved = memberService.create(
                TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nation.getId()));

        mockMvc.perform(delete("/api/alliance-members/{id}", saved.getId())
                        .header("Authorization", authHeader()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/alliance-members/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatCreateMemberWithoutAuthReturnsHttp401() throws Exception {
        AllianceDto alliance = savedAlliance();
        NationDto nation = savedNation();
        String json = objectMapper.writeValueAsString(
                TestDataUtil.createTestAllianceMemberDtoA(alliance.getId(), nation.getId()));

        mockMvc.perform(post("/api/alliance-members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }
}
