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
        // Starts the JavaFX Platform
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already started
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        service = new StiService();

        // Inject mock database (reflection)
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
    // 🟩 searchByNameAsync — Boundary + Partition
    // =====================================================

    @Test
    void searchByName_validKeyword_shouldFilter() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, "hiv",
                result -> {
                    assertEquals(1, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByName_nullKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, null,
                result -> {
                    assertEquals(2, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByName_emptyKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, "",
                result -> {
                    assertEquals(2, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByName_caseInsensitive_shouldWork() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByNameAsync(data, "HIV",
                result -> {
                    assertEquals(1, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 searchBySymptomsAsync — Boundary + Partition
    // =====================================================

    @Test
    void searchBySymptoms_validKeyword_shouldFilter() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, "fever",
                result -> {
                    assertEquals(1, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchBySymptoms_nullKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, null,
                result -> {
                    assertEquals(1, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchBySymptoms_invalidKeyword_shouldReturnEmpty() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, "xyz",
                result -> {
                    assertTrue(result.isEmpty());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // ⚠️ BUG TEST
    @Test
    void searchBySymptoms_nullSymptoms_shouldThrowException() throws InterruptedException {
        when(entry1.getSymptoms()).thenReturn(null);

        List<StiEntry> data = List.of(entry1);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchBySymptomsAsync(data, "fever",
                r -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 searchByRiskLevelAsync — Boundary + Partition
    // =====================================================

    @Test
    void searchByRiskLevel_valid_shouldFilter() throws InterruptedException {
        List<StiEntry> data = List.of(entry1, entry2);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, "3",
                result -> {
                    assertEquals(1, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByRiskLevel_invalidNumber_shouldReturnEmpty() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, "abc",
                result -> {
                    assertTrue(result.isEmpty());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByRiskLevel_nullKeyword_shouldReturnAll() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, null,
                result -> {
                    assertEquals(1, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void searchByRiskLevel_negative_shouldReturnEmpty() throws InterruptedException {
        List<StiEntry> data = List.of(entry1);

        CountDownLatch latch = new CountDownLatch(1);

        service.searchByRiskLevelAsync(data, "-1",
                result -> {
                    assertTrue(result.isEmpty());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 getAllAsync — Limit + Async
    // =====================================================

    @Test
    void getAllAsync_largeDataset_shouldHandle() throws InterruptedException {
        ArrayList<StiEntry> bigList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            bigList.add(mock(StiEntry.class));
        }

        when(mockDatabase.getAll()).thenReturn(bigList);

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllAsync(
                list -> {
                    assertEquals(10000, list.size());
                    latch.countDown();
                },
                e -> fail()
        );

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
    // 🟩 Async Edge Case
    // =====================================================

    @Test
    void nullCallbacks_shouldNotCrash() {
        assertDoesNotThrow(() ->
                service.getAllAsync(null, null)
        );
    }
}