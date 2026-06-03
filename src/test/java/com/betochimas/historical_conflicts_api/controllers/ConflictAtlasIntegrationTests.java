package com.betochimas.historical_conflicts_api.controllers;

import com.betochimas.historical_conflicts_api.AbstractIntegrationTest;
import com.betochimas.historical_conflicts_api.TestDataUtil;
import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.service.BattleService;
import com.betochimas.historical_conflicts_api.service.ConflictParticipantService;
import com.betochimas.historical_conflicts_api.service.ConflictService;
import com.betochimas.historical_conflicts_api.service.NationService;
import com.betochimas.historical_conflicts_api.service.TheaterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The aggregate {@code GET /api/conflicts/{id}/atlas} endpoint (slice B2): shape, date-sorted
 * battles with contiguous seq, computed stats, 404 on a missing conflict, public readability,
 * and that the (uncached) atlas reflects later writes to its sub-entities.
 */
public class ConflictAtlasIntegrationTests extends AbstractIntegrationTest {

    private final MockMvc mockMvc;
    private final ConflictService conflictService;
    private final NationService nationService;
    private final BattleService battleService;
    private final TheaterService theaterService;
    private final ConflictParticipantService participantService;

    @Autowired
    public ConflictAtlasIntegrationTests(MockMvc mockMvc,
                                         ConflictService conflictService,
                                         NationService nationService,
                                         BattleService battleService,
                                         TheaterService theaterService,
                                         ConflictParticipantService participantService) {
        this.mockMvc = mockMvc;
        this.conflictService = conflictService;
        this.nationService = nationService;
        this.battleService = battleService;
        this.theaterService = theaterService;
        this.participantService = participantService;
    }

    /**
     * Seeds a WWI conflict with two participant nations, one theater, and three battles created
     * out of date order (Somme, Marne, Verdun) to prove the atlas sorts by date. Marne is assigned
     * to the theater. Returns the conflict id.
     */
    private Long seedWwiAtlas() {
        Long conflictId = conflictService.create(TestDataUtil.createTestConflictDtoA()).getId();

        Long nationA = nationService.create(TestDataUtil.createTestNationDtoA()).getId();
        Long nationB = nationService.create(TestDataUtil.createTestNationDtoB()).getId();
        // A: troops 4,000,000 / casualties 1,800,000 ; B: troops 3,500,000 / casualties 1,400,000.
        participantService.create(TestDataUtil.createTestConflictParticipantDtoA(conflictId, nationA));
        participantService.create(TestDataUtil.createTestConflictParticipantDtoB(conflictId, nationB));

        Long theaterId = theaterService.create(TestDataUtil.createTestTheaterDtoA(conflictId)).getId();

        battleService.create(TestDataUtil.createTestBattleDtoC(conflictId));   // Somme  1916-07-01
        BattleDto marne = TestDataUtil.createTestBattleDtoA(conflictId);       // Marne  1914-09-05 (+coords)
        marne.setTheaterId(theaterId);
        battleService.create(marne);
        battleService.create(TestDataUtil.createTestBattleDtoB(conflictId));   // Verdun 1916-02-21

        return conflictId;
    }

    @Test
    public void testThatAtlasReturnsConflictTheatersBattlesParticipantsAndStats() throws Exception {
        Long id = seedWwiAtlas();

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conflict.id").value(id))
                .andExpect(jsonPath("$.conflict.name").value("World War I"))
                .andExpect(jsonPath("$.conflict.conflictType").value("WAR"))
                .andExpect(jsonPath("$.battles.length()").value(3))
                .andExpect(jsonPath("$.theaters.length()").value(1))
                .andExpect(jsonPath("$.participants.length()").value(2))
                // Participants carry their coalition side (order-independent; drives map coalition colors).
                .andExpect(jsonPath("$.participants[*].side",
                        containsInAnyOrder("Allied Powers", "Central Powers")))
                .andExpect(jsonPath("$.stats.totalBattles").value(3))
                .andExpect(jsonPath("$.stats.totalTheaters").value(1))
                .andExpect(jsonPath("$.stats.totalParticipants").value(2))
                .andExpect(jsonPath("$.stats.totalCasualties").value(3_200_000))
                .andExpect(jsonPath("$.stats.totalTroopsCommitted").value(7_500_000))
                .andExpect(jsonPath("$.stats.durationDays").isNumber());
    }

    @Test
    public void testThatAtlasSortsBattlesByDateWithContiguousSeqAndCoordsAndTheaterLink() throws Exception {
        Long id = seedWwiAtlas();

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id))
                .andExpect(status().isOk())
                // Sorted ascending by date regardless of insertion order: Marne < Verdun < Somme.
                .andExpect(jsonPath("$.battles[0].name").value("Battle of the Marne"))
                .andExpect(jsonPath("$.battles[0].seq").value(1))
                .andExpect(jsonPath("$.battles[0].latitude").value(48.96))
                .andExpect(jsonPath("$.battles[0].longitude").value(3.39))
                .andExpect(jsonPath("$.battles[0].theaterId").isNumber())
                .andExpect(jsonPath("$.battles[1].name").value("Battle of Verdun"))
                .andExpect(jsonPath("$.battles[1].seq").value(2))
                .andExpect(jsonPath("$.battles[2].name").value("Battle of the Somme"))
                .andExpect(jsonPath("$.battles[2].seq").value(3))
                // The theater groups exactly the one battle assigned to it (the Marne).
                .andExpect(jsonPath("$.theaters[0].battleIds.length()").value(1));
    }

    @Test
    public void testThatAtlasReturnsHttp404WhenConflictMissing() throws Exception {
        mockMvc.perform(get("/api/conflicts/{id}/atlas", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Conflict not found: 999"));
    }

    @Test
    public void testThatAtlasIsPubliclyReadableWithoutAuth() throws Exception {
        Long id = seedWwiAtlas();

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatANewBattleIsReflectedInTheAtlas() throws Exception {
        Long id = seedWwiAtlas();

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id))
                .andExpect(jsonPath("$.stats.totalBattles").value(3));

        battleService.create(TestDataUtil.createTestBattleDtoB(id));

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id))
                .andExpect(jsonPath("$.stats.totalBattles").value(4));
    }

    @Test
    public void testThatANewParticipantIsReflectedInTheAtlasStats() throws Exception {
        Long id = seedWwiAtlas();

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id))
                .andExpect(jsonPath("$.stats.totalParticipants").value(2));

        Long nationC = nationService.create(TestDataUtil.createTestNationDtoC()).getId();
        participantService.create(TestDataUtil.createTestConflictParticipantDtoC(id, nationC));  // +900,000 casualties

        mockMvc.perform(get("/api/conflicts/{id}/atlas", id))
                .andExpect(jsonPath("$.stats.totalParticipants").value(3))
                .andExpect(jsonPath("$.stats.totalCasualties").value(4_100_000));
    }
}
