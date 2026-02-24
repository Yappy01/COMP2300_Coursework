package Service;

import Models.StiEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class StiServiceTest {
    private StiService service;
    private List<StiEntry> fakeData;

    @BeforeEach
    void setUp() {
        service = new StiService();

        fakeData = List.of(
                new StiEntry(1, "HIV", "fever", "...", "...", 5),
                new StiEntry(2, "HPV", "warts", "...", "...", 3),
                new StiEntry(3, "Syphilis", "rash", "...", "...", 4)
        );
    }

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
}
