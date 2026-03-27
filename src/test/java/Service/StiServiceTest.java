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

    // --- 1. EXISTING TESTS ---
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

    // --- 2. ADDED BOUNDARY & LIMIT TESTING ---

    @Test
    @DisplayName("Boundary: Null keyword should return the original full list")
    void searchByName_nullKeyword_returnsAll() {
        List<StiEntry> result = service.searchByName(fakeData, null);
        assertEquals(fakeData.size(), result.size(), "Should return full list on null input");
    }

    @Test
    @DisplayName("Boundary: Empty string or whitespace should return full list")
    void searchBySymptoms_blankKeyword_returnsAll() {
        List<StiEntry> result = service.searchBySymptoms(fakeData, "   ");
        assertEquals(fakeData.size(), result.size(), "Should return full list on blank input");
    }

    @Test
    @DisplayName("Unit: Partial match and case insensitivity in symptoms")
    void searchBySymptoms_partialMatch_caseInsensitive() {
        // Search "FEVER" (uppercase) should find two entries containing "fever"
        List<StiEntry> result = service.searchBySymptoms(fakeData, "FEVER");
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Limit: Searching an empty dataset")
    void search_onEmptyList_returnsEmpty() {
        List<StiEntry> emptyList = new ArrayList<>();
        List<StiEntry> result = service.searchByName(emptyList, "HIV");
        assertTrue(result.isEmpty(), "Searching an empty list should return an empty result");
    }

    @Test
    @DisplayName("Limit: Risk level search with no matching value")
    void searchByRiskLevel_noMatch_returnsEmpty() {
        // We have 3, 4, 5. Search for 1.
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "1");
        assertTrue(result.isEmpty(), "Should return empty list if no entry has that risk level");
    }

    @Test
    @DisplayName("Boundary: Handling special characters in search")
    void searchByName_specialCharacters() {
        // Ensure symbols don't crash the regex/stream logic
        assertDoesNotThrow(() -> service.searchByName(fakeData, "!@#$%^&*()"));
        List<StiEntry> result = service.searchByName(fakeData, "!!!");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Boundary: Large numeric input for risk level")
    void searchByRiskLevel_largeNumber() {
        // Test a number larger than Integer.MAX_VALUE to see if ParseInt throws error
        // Your service catches NumberFormatException, so it should return empty
        List<StiEntry> result = service.searchByRiskLevel(fakeData, "9999999999999999");
        assertTrue(result.isEmpty());
    }
}