package Service;

import Models.StiEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class StiServiceTest {
    private StiService service;
    private List<StiEntry> fakeData;

    @BeforeEach
    void setUp() {
        service = new StiService();

        fakeData = List.of(
                new StiEntry(1, "HIV", "fever, chills", "...", "...", 5),
                new StiEntry(2, "HPV", "warts", "...", "...", 3),
                new StiEntry(3, "Syphilis", "rash, fever", "...", "...", 4)
        );
    }

    // --- 1. FUNCTIONAL TESTING ---

    @Test
    void searchByName_shouldReturnMatch() {
        List<StiEntry> result = service.searchByName(fakeData, "hiv");
        assertEquals(1, result.size());
        assertEquals("HIV", result.get(0).getName());
    }

    @Test
    void searchByRiskLevel_shouldReturnCorrectRisk() {
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "3");
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getRiskLevel());
    }

    @Test
    void searchByRiskLevel_invalidInput_shouldReturnEmptyList() {
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "abc");
        assertTrue(result.isEmpty());
    }

    // --- 2. PARTITION TESTING ---

    @Test
    @DisplayName("Partition: Null keyword (name search)")
    void searchByName_nullKeyword_returnsAll() {
        List<StiEntry> result = service.searchByName(fakeData, null);
        assertEquals(fakeData.size(), result.size());
    }

    @Test
    @DisplayName("Partition: Empty keyword")
    void searchByName_emptyKeyword_returnsAll() {
        List<StiEntry> result = service.searchByName(fakeData, "");
        assertEquals(fakeData.size(), result.size());
    }

    @Test
    @DisplayName("Partition: Whitespace keyword")
    void searchBySymptoms_blankKeyword_returnsAll() {
        List<StiEntry> result = service.searchBySymptoms(fakeData, "   ");
        assertEquals(fakeData.size(), result.size());
    }

    @Test
    @DisplayName("Partition: Special characters")
    void searchByName_specialCharacters() {
        assertDoesNotThrow(() -> service.searchByName(fakeData, "!@#$%^&*()"));
        List<StiEntry> result = service.searchByName(fakeData, "!!!");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Partition: Case insensitivity")
    void searchBySymptoms_caseInsensitive() {
        List<StiEntry> result = service.searchBySymptoms(fakeData, "FEVER");
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Partition: Partial match")
    void searchByName_partialMatch() {
        List<StiEntry> result = service.searchByName(fakeData, "syph");
        assertEquals(1, result.size());
    }

    // --- 3. LIMIT TESTING ---

    @Test
    @DisplayName("Limit: Empty dataset")
    void search_onEmptyList_returnsEmpty() {
        List<StiEntry> emptyList = new ArrayList<>();
        List<StiEntry> result = service.searchByName(emptyList, "HIV");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Limit: No matching results")
    void searchByRiskLevel_noMatch_returnsEmpty() {
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "1");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Limit: Very large keyword input")
    void searchByName_veryLongInput() {
        String longInput = "A".repeat(10000);
        assertDoesNotThrow(() -> service.searchByName(fakeData, longInput));
    }

    @Test
    @DisplayName("Limit: Very large numeric input for risk level")
    void searchByRiskLevel_largeNumber() {
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "9999999999999999");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Limit: Minimum valid risk level")
    void searchByRiskLevel_minBoundary() {
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "0");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Limit: Maximum valid risk level in dataset")
    void searchByRiskLevel_maxBoundary() {
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "5");
        assertEquals(1, result.size());
    }

    // --- 4. NEGATIVE TESTING ---

    @Test
    @DisplayName("Negative: Null input for risk level")
    void searchByRiskLevel_nullInput() {
        List<StiEntry> result = service.searchByRiskLevel(fakeData, null);
        assertTrue(result.equals(fakeData));
    }
}