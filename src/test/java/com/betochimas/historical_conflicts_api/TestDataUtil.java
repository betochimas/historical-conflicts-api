package com.betochimas.historical_conflicts_api;

import com.betochimas.historical_conflicts_api.domain.dto.BattleDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictDto;
import com.betochimas.historical_conflicts_api.domain.dto.ConflictParticipantDto;
import com.betochimas.historical_conflicts_api.domain.dto.LeaderDto;
import com.betochimas.historical_conflicts_api.domain.dto.NationDto;
import com.betochimas.historical_conflicts_api.domain.dto.TheaterDto;
import com.betochimas.historical_conflicts_api.domain.model.BattleEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictParticipantEntity;
import com.betochimas.historical_conflicts_api.domain.model.ConflictType;
import com.betochimas.historical_conflicts_api.domain.model.LeaderEntity;
import com.betochimas.historical_conflicts_api.domain.model.LeaderRole;
import com.betochimas.historical_conflicts_api.domain.model.NationEntity;
import com.betochimas.historical_conflicts_api.domain.model.ParticipantRole;
import com.betochimas.historical_conflicts_api.domain.model.TheaterEntity;

import java.time.LocalDate;

public final class TestDataUtil {

    private TestDataUtil() {}

    // --- Nation Entities ---

    public static NationEntity createTestNationEntityA() {
        NationEntity entity = new NationEntity();
        entity.setName("Roman Empire");
        entity.setRegion("Europe");
        entity.setFoundedYear(-27);
        entity.setDissolvedYear(476);
        entity.setDescription("Ancient Mediterranean superpower centred on Rome.");
        return entity;
    }

    public static NationEntity createTestNationEntityB() {
        NationEntity entity = new NationEntity();
        entity.setName("Ottoman Empire");
        entity.setRegion("Middle East & Europe");
        entity.setFoundedYear(1299);
        entity.setDissolvedYear(1922);
        entity.setDescription("Transcontinental empire lasting over six centuries.");
        return entity;
    }

    public static NationEntity createTestNationEntityC() {
        NationEntity entity = new NationEntity();
        entity.setName("British Empire");
        entity.setRegion("Global");
        entity.setFoundedYear(1583);
        entity.setDissolvedYear(1997);
        entity.setDescription("Largest empire in history at its peak.");
        return entity;
    }

    // --- Nation DTOs ---

    public static NationDto createTestNationDtoA() {
        return NationDto.builder()
                .name("Roman Empire")
                .region("Europe")
                .foundedYear(-27)
                .dissolvedYear(476)
                .description("Ancient Mediterranean superpower centred on Rome.")
                .build();
    }

    public static NationDto createTestNationDtoB() {
        return NationDto.builder()
                .name("Ottoman Empire")
                .region("Middle East & Europe")
                .foundedYear(1299)
                .dissolvedYear(1922)
                .description("Transcontinental empire lasting over six centuries.")
                .build();
    }

    public static NationDto createTestNationDtoC() {
        return NationDto.builder()
                .name("British Empire")
                .region("Global")
                .foundedYear(1583)
                .dissolvedYear(1997)
                .description("Largest empire in history at its peak.")
                .build();
    }

    // --- Conflict Entities ---

    public static ConflictEntity createTestConflictEntityA() {
        ConflictEntity entity = new ConflictEntity();
        entity.setName("World War I");
        entity.setConflictType(ConflictType.WAR);
        entity.setStartDate(LocalDate.of(1914, 7, 28));
        entity.setEndDate(LocalDate.of(1918, 11, 11));
        entity.setOutcome("Allied victory");
        entity.setDescription("Global war originating in Europe.");
        return entity;
    }

    public static ConflictEntity createTestConflictEntityB() {
        ConflictEntity entity = new ConflictEntity();
        entity.setName("Hundred Years War");
        entity.setConflictType(ConflictType.WAR);
        entity.setStartDate(LocalDate.of(1337, 5, 24));
        entity.setEndDate(LocalDate.of(1453, 10, 19));
        entity.setOutcome("French victory");
        entity.setDescription("Series of conflicts between England and France.");
        return entity;
    }

    public static ConflictEntity createTestConflictEntityC() {
        ConflictEntity entity = new ConflictEntity();
        entity.setName("American Civil War");
        entity.setConflictType(ConflictType.CIVIL_WAR);
        entity.setStartDate(LocalDate.of(1861, 4, 12));
        entity.setEndDate(LocalDate.of(1865, 4, 9));
        entity.setOutcome("Union victory");
        entity.setDescription("Civil war between Union and Confederate states.");
        return entity;
    }

    // --- Conflict DTOs ---

    public static ConflictDto createTestConflictDtoA() {
        return ConflictDto.builder()
                .name("World War I")
                .conflictType(ConflictType.WAR)
                .startDate(LocalDate.of(1914, 7, 28))
                .endDate(LocalDate.of(1918, 11, 11))
                .outcome("Allied victory")
                .description("Global war originating in Europe.")
                .build();
    }

    public static ConflictDto createTestConflictDtoB() {
        return ConflictDto.builder()
                .name("Hundred Years War")
                .conflictType(ConflictType.WAR)
                .startDate(LocalDate.of(1337, 5, 24))
                .endDate(LocalDate.of(1453, 10, 19))
                .outcome("French victory")
                .description("Series of conflicts between England and France.")
                .build();
    }

    public static ConflictDto createTestConflictDtoC() {
        return ConflictDto.builder()
                .name("American Civil War")
                .conflictType(ConflictType.CIVIL_WAR)
                .startDate(LocalDate.of(1861, 4, 12))
                .endDate(LocalDate.of(1865, 4, 9))
                .outcome("Union victory")
                .description("Civil war between Union and Confederate states.")
                .build();
    }

    // --- Battle Entities ---

    public static BattleEntity createTestBattleEntityA(ConflictEntity conflict) {
        BattleEntity entity = new BattleEntity();
        entity.setConflict(conflict);
        entity.setName("Battle of the Marne");
        entity.setDate(LocalDate.of(1914, 9, 5));
        entity.setLocation("Marne River, France");
        entity.setTerrain("River valley");
        entity.setOutcome("Allied victory");
        entity.setDescription("Halted the German advance into France.");
        return entity;
    }

    public static BattleEntity createTestBattleEntityB(ConflictEntity conflict) {
        BattleEntity entity = new BattleEntity();
        entity.setConflict(conflict);
        entity.setName("Battle of Verdun");
        entity.setDate(LocalDate.of(1916, 2, 21));
        entity.setLocation("Verdun, France");
        entity.setTerrain("Fortified hills");
        entity.setOutcome("French victory");
        entity.setDescription("One of the longest battles of WWI.");
        return entity;
    }

    public static BattleEntity createTestBattleEntityC(ConflictEntity conflict) {
        BattleEntity entity = new BattleEntity();
        entity.setConflict(conflict);
        entity.setName("Battle of the Somme");
        entity.setDate(LocalDate.of(1916, 7, 1));
        entity.setLocation("Somme, France");
        entity.setTerrain("Open fields and trenches");
        entity.setOutcome("Inconclusive");
        entity.setDescription("One of the largest battles of WWI.");
        return entity;
    }

    // --- Battle DTOs ---

    public static BattleDto createTestBattleDtoA(Long conflictId) {
        return BattleDto.builder()
                .conflictId(conflictId)
                .name("Battle of the Marne")
                .date(LocalDate.of(1914, 9, 5))
                .location("Marne River, France")
                .terrain("River valley")
                .outcome("Allied victory")
                .description("Halted the German advance into France.")
                .build();
    }

    public static BattleDto createTestBattleDtoB(Long conflictId) {
        return BattleDto.builder()
                .conflictId(conflictId)
                .name("Battle of Verdun")
                .date(LocalDate.of(1916, 2, 21))
                .location("Verdun, France")
                .terrain("Fortified hills")
                .outcome("French victory")
                .description("One of the longest battles of WWI.")
                .build();
    }

    public static BattleDto createTestBattleDtoC(Long conflictId) {
        return BattleDto.builder()
                .conflictId(conflictId)
                .name("Battle of the Somme")
                .date(LocalDate.of(1916, 7, 1))
                .location("Somme, France")
                .terrain("Open fields and trenches")
                .outcome("Inconclusive")
                .description("One of the largest battles of WWI.")
                .build();
    }

    // --- ConflictParticipant Entities ---

    public static ConflictParticipantEntity createTestConflictParticipantEntityA(
            ConflictEntity conflict, NationEntity nation) {
        ConflictParticipantEntity entity = new ConflictParticipantEntity();
        entity.setConflict(conflict);
        entity.setNation(nation);
        entity.setRole(ParticipantRole.ATTACKER);
        entity.setTroopsCommitted(4_000_000);
        entity.setCasualties(1_800_000);
        entity.setOutcome("Victory");
        return entity;
    }

    public static ConflictParticipantEntity createTestConflictParticipantEntityB(
            ConflictEntity conflict, NationEntity nation) {
        ConflictParticipantEntity entity = new ConflictParticipantEntity();
        entity.setConflict(conflict);
        entity.setNation(nation);
        entity.setRole(ParticipantRole.DEFENDER);
        entity.setTroopsCommitted(3_500_000);
        entity.setCasualties(1_400_000);
        entity.setOutcome("Defeat");
        return entity;
    }

    public static ConflictParticipantEntity createTestConflictParticipantEntityC(
            ConflictEntity conflict, NationEntity nation) {
        ConflictParticipantEntity entity = new ConflictParticipantEntity();
        entity.setConflict(conflict);
        entity.setNation(nation);
        entity.setRole(ParticipantRole.ALLY);
        entity.setTroopsCommitted(2_000_000);
        entity.setCasualties(900_000);
        entity.setOutcome("Victory");
        return entity;
    }

    // --- ConflictParticipant DTOs ---

    public static ConflictParticipantDto createTestConflictParticipantDtoA(
            Long conflictId, Long nationId) {
        return ConflictParticipantDto.builder()
                .conflictId(conflictId)
                .nationId(nationId)
                .role(ParticipantRole.ATTACKER)
                .troopsCommitted(4_000_000)
                .casualties(1_800_000)
                .outcome("Victory")
                .build();
    }

    public static ConflictParticipantDto createTestConflictParticipantDtoB(
            Long conflictId, Long nationId) {
        return ConflictParticipantDto.builder()
                .conflictId(conflictId)
                .nationId(nationId)
                .role(ParticipantRole.DEFENDER)
                .troopsCommitted(3_500_000)
                .casualties(1_400_000)
                .outcome("Defeat")
                .build();
    }

    public static ConflictParticipantDto createTestConflictParticipantDtoC(
            Long conflictId, Long nationId) {
        return ConflictParticipantDto.builder()
                .conflictId(conflictId)
                .nationId(nationId)
                .role(ParticipantRole.ALLY)
                .troopsCommitted(2_000_000)
                .casualties(900_000)
                .outcome("Victory")
                .build();
    }

    // --- Theater Entities ---

    public static TheaterEntity createTestTheaterEntityA(ConflictEntity conflict) {
        TheaterEntity entity = new TheaterEntity();
        entity.setConflict(conflict);
        entity.setName("Western Front");
        entity.setRegion("France & Belgium");
        entity.setStartDate(LocalDate.of(1914, 8, 4));
        entity.setEndDate(LocalDate.of(1918, 11, 11));
        entity.setOutcome("Allied victory");
        entity.setDescription("The decisive theater of the war.");
        return entity;
    }

    public static TheaterEntity createTestTheaterEntityB(ConflictEntity conflict) {
        TheaterEntity entity = new TheaterEntity();
        entity.setConflict(conflict);
        entity.setName("Eastern Front");
        entity.setRegion("Eastern Europe");
        entity.setStartDate(LocalDate.of(1914, 8, 17));
        entity.setEndDate(LocalDate.of(1918, 3, 3));
        entity.setOutcome("Central Powers victory");
        entity.setDescription("A war of movement between the Central Powers and Russia.");
        return entity;
    }

    public static TheaterEntity createTestTheaterEntityC(ConflictEntity conflict) {
        TheaterEntity entity = new TheaterEntity();
        entity.setConflict(conflict);
        entity.setName("Gallipoli & Middle East");
        entity.setRegion("Ottoman Empire");
        entity.setStartDate(LocalDate.of(1915, 4, 25));
        entity.setEndDate(LocalDate.of(1918, 10, 30));
        entity.setOutcome("Mixed");
        entity.setDescription("Allied operations against the Ottoman Empire.");
        return entity;
    }

    // --- Theater DTOs ---

    public static TheaterDto createTestTheaterDtoA(Long conflictId) {
        return TheaterDto.builder()
                .conflictId(conflictId)
                .name("Western Front")
                .region("France & Belgium")
                .startDate(LocalDate.of(1914, 8, 4))
                .endDate(LocalDate.of(1918, 11, 11))
                .outcome("Allied victory")
                .description("The decisive theater of the war.")
                .build();
    }

    public static TheaterDto createTestTheaterDtoB(Long conflictId) {
        return TheaterDto.builder()
                .conflictId(conflictId)
                .name("Eastern Front")
                .region("Eastern Europe")
                .startDate(LocalDate.of(1914, 8, 17))
                .endDate(LocalDate.of(1918, 3, 3))
                .outcome("Central Powers victory")
                .description("A war of movement between the Central Powers and Russia.")
                .build();
    }

    public static TheaterDto createTestTheaterDtoC(Long conflictId) {
        return TheaterDto.builder()
                .conflictId(conflictId)
                .name("Gallipoli & Middle East")
                .region("Ottoman Empire")
                .startDate(LocalDate.of(1915, 4, 25))
                .endDate(LocalDate.of(1918, 10, 30))
                .outcome("Mixed")
                .description("Allied operations against the Ottoman Empire.")
                .build();
    }

    // --- Leader Entities ---

    public static LeaderEntity createTestLeaderEntityA(NationEntity nation) {
        LeaderEntity entity = new LeaderEntity();
        entity.setNation(nation);
        entity.setName("Woodrow Wilson");
        entity.setRole(LeaderRole.HEAD_OF_STATE);
        entity.setTitle("President of the United States");
        entity.setBirthYear(1856);
        entity.setDeathYear(1924);
        entity.setDescription("Led the US into WWI and championed the Fourteen Points.");
        return entity;
    }

    public static LeaderEntity createTestLeaderEntityB(NationEntity nation) {
        LeaderEntity entity = new LeaderEntity();
        entity.setNation(nation);
        entity.setName("Ferdinand Foch");
        entity.setRole(LeaderRole.GENERAL);
        entity.setTitle("Marshal of France");
        entity.setBirthYear(1851);
        entity.setDeathYear(1929);
        entity.setDescription("Supreme Allied Commander on the Western Front from April 1918.");
        return entity;
    }

    public static LeaderEntity createTestLeaderEntityC(NationEntity nation) {
        LeaderEntity entity = new LeaderEntity();
        entity.setNation(nation);
        entity.setName("Nicholas II");
        entity.setRole(LeaderRole.MONARCH);
        entity.setTitle("Tsar of Russia");
        entity.setBirthYear(1868);
        entity.setDeathYear(1918);
        entity.setDescription("Last Russian emperor; overthrown in the February Revolution.");
        return entity;
    }

    // --- Leader DTOs ---

    public static LeaderDto createTestLeaderDtoA(Long nationId) {
        return LeaderDto.builder()
                .nationId(nationId)
                .name("Woodrow Wilson")
                .role(LeaderRole.HEAD_OF_STATE)
                .title("President of the United States")
                .birthYear(1856)
                .deathYear(1924)
                .description("Led the US into WWI and championed the Fourteen Points.")
                .build();
    }

    public static LeaderDto createTestLeaderDtoB(Long nationId) {
        return LeaderDto.builder()
                .nationId(nationId)
                .name("Ferdinand Foch")
                .role(LeaderRole.GENERAL)
                .title("Marshal of France")
                .birthYear(1851)
                .deathYear(1929)
                .description("Supreme Allied Commander on the Western Front from April 1918.")
                .build();
    }

    public static LeaderDto createTestLeaderDtoC(Long nationId) {
        return LeaderDto.builder()
                .nationId(nationId)
                .name("Nicholas II")
                .role(LeaderRole.MONARCH)
                .title("Tsar of Russia")
                .birthYear(1868)
                .deathYear(1918)
                .description("Last Russian emperor; overthrown in the February Revolution.")
                .build();
    }
}
