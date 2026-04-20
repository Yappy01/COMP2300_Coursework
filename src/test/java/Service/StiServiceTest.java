package Service;

import DBHandling.StiDatabase;
import Models.StiEntry;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StiServiceTest {

    private StiService service;

    @Mock
    private StiDatabase mockDatabase;

    @Mock
    private StiEntry entry1;

    @Mock
    private StiEntry entry2;

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // already started
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        service = new StiService();

        var field = StiService.class.getDeclaredField("stiDatabase");
        field.setAccessible(true);
        field.set(service, mockDatabase);

        when(entry1.getName()).thenReturn("HIV");
        when(entry1.getSymptoms()).thenReturn("Fever cough");
        when(entry1.getRiskLevel()).thenReturn(3);

        when(entry2.getName()).thenReturn("Chlamydia");
        when(entry2.getSymptoms()).thenReturn("Pain discharge");
        when(entry2.getRiskLevel()).thenReturn(1);
    }

    // =====================================================
    // 🟩 searchByNameAsync
    // =====================================================

    @Test
    void searchByName_validKeyword_shouldFilter() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, "hiv", result -> {
            assertEquals(1, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByName_nullKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, null, result -> {
            assertEquals(2, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByName_emptyKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, "", result -> {
            assertEquals(2, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // ✅ Boundary: single character
    @Test
    void searchByName_singleCharacterBoundary() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, "h", result -> {
            assertEquals(1, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // ✅ Partition: null name inside data
    @Test
    void searchByName_nullNameInsideData_shouldNotCrash() throws InterruptedException {
        when(entry1.getName()).thenReturn(null);

        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, "hiv", result -> {
            assertNotNull(result);
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 searchBySymptomsAsync
    // =====================================================

    @Test
    void searchBySymptoms_validKeyword_shouldFilter() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, "fever", result -> {
            assertEquals(1, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchBySymptoms_nullKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, null, result -> {
            assertEquals(1, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchBySymptoms_invalidKeyword_shouldReturnEmpty() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, "xyz", result -> {
            assertTrue(result.isEmpty());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // 🔥 FIXED expectation (should NOT crash ideally)
    @Test
    void searchBySymptoms_noSymptoms_shouldNotCrash() throws InterruptedException {
        when(entry1.getSymptoms()).thenReturn("");

        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, "fever",
                result -> {
                    assertNotNull(result);
                    latch.countDown();
                },
                e -> {
                    fail("Should not throw exception");
                    latch.countDown();
                });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 searchByRiskLevelAsync
    // =====================================================

    @Test
    void searchByRiskLevel_valid_shouldFilter() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, "3", result -> {
            assertEquals(1, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByRiskLevel_invalidNumber_shouldReturnEmpty() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, "abc", result -> {
            assertTrue(result.isEmpty());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByRiskLevel_largeNumberBoundary() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, "999999", result -> {
            assertTrue(result.isEmpty());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByRiskLevel_nullKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);
        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, null, result -> {
            assertEquals(1, result.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 getAllAsync
    // =====================================================

    @Test
    void getAllAsync_largeDataset_shouldHandle() throws InterruptedException {
        ArrayList<StiEntry> bigList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            bigList.add(mock(StiEntry.class));
        }

        when(mockDatabase.getAll()).thenReturn(bigList);

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllAsync(list -> {
            assertEquals(10000, list.size());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    @Test
    void getAllAsync_dbFailure_shouldTriggerOnFailed() throws InterruptedException {
        when(mockDatabase.getAll()).thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllAsync(
                list -> fail(),
                e -> latch.countDown()
        );

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 Stress / Limit Test
    // =====================================================

    @Test
    void searchByName_largeDataset_shouldPerform() throws InterruptedException {
        List<StiEntry> bigList = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            StiEntry e = mock(StiEntry.class);
            when(e.getName()).thenReturn("test" + i);
            bigList.add(e);
        }

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(bigList, "test1", result -> {
            assertFalse(result.isEmpty());
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 Async Edge Case
    // =====================================================

    @Test
    void nullCallbacks_shouldNotCrash() {
        assertDoesNotThrow(() ->
                service.getAllAsync(null, null)
        );
    }
}