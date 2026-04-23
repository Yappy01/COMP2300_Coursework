package Service;

import DBHandling.StiDatabase;
import Models.StiEntry;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StiServiceTest {

    private StiService service;

    @Mock private StiDatabase mockDatabase;
    @Mock private StiEntry entry1;
    @Mock private StiEntry entry2;

    private static final int ASYNC_TIMEOUT = 3;

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
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

    @Nested
    class SearchTests {
        @Test
        void searchByName_valid_shouldFilter() throws InterruptedException {
            List<StiEntry> data = List.of(entry1, entry2);
            CountDownLatch latch = new CountDownLatch(1);

            service.searchByNameAsync(data, "hiv", result -> {
                assertEquals(1, result.size());
                assertEquals("HIV", result.get(0).getName());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void searchByName_empty_shouldReturnAll(String keyword) throws InterruptedException {
            List<StiEntry> data = List.of(entry1, entry2);
            CountDownLatch latch = new CountDownLatch(1);

            service.searchByNameAsync(data, keyword, result -> {
                assertEquals(2, result.size());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void searchByRiskLevel_valid_shouldFilter() throws InterruptedException {
            List<StiEntry> data = List.of(entry1, entry2);
            CountDownLatch latch = new CountDownLatch(1);

            service.searchByRiskLevelAsync(data, "3", result -> {
                assertEquals(1, result.size());
                assertEquals(3, result.get(0).getRiskLevel());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void searchByRiskLevel_invalidString_shouldReturnEmpty() throws InterruptedException {
            List<StiEntry> data = List.of(entry1);
            CountDownLatch latch = new CountDownLatch(1);

            service.searchByRiskLevelAsync(data, "dangerous", result -> {
                assertTrue(result.isEmpty());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class ValidationTests {
        @Test
        void validateInput_missingFields_shouldReturnError() {
            String result = service.validateInput("", "symptoms", "treatment", "prevention", "1");
            assertEquals("Please fill in all the blanks", result);
        }

        @Test
        void validateInput_duplicateName_shouldReturnError() {
            when(mockDatabase.findByName("HIV")).thenReturn(new StiEntry());
            String result = service.validateInput("HIV", "s", "t", "p", "1");
            assertTrue(result.contains("already exists"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "5"})
        void validateInput_riskBoundary_shouldReturnError(String risk) {
            String result = service.validateInput("NewSTI", "s", "t", "p", risk);
            assertEquals("risk level should be between 0 - 4", result);
        }

        @Test
        void validateInput_perfectInput_shouldPass() {
            when(mockDatabase.findByName("UniqueSTI")).thenReturn(null);
            String result = service.validateInput("UniqueSTI", "s", "t", "p", "2");
            assertEquals("", result);
        }
    }

    @Nested
    class DatabaseTests {
        @Test
        void deleteStiAsync_shouldReturnTrue() throws InterruptedException {
            when(mockDatabase.delete(1)).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);

            service.deleteStiAsync(1, result -> {
                assertTrue(result);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
            verify(mockDatabase).delete(1);
        }

        @Test
        void insertStiInfo_shouldCallDatabase() throws InterruptedException {
            StiEntry entry = new StiEntry();
            when(mockDatabase.addSti(entry)).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);

            service.insertStiInfo(entry, result -> {
                assertTrue(result);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
            verify(mockDatabase).addSti(entry);
        }

        @Test
        void getAllAsync_largeData_shouldSucceed() throws InterruptedException {
            ArrayList<StiEntry> bigList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) bigList.add(mock(StiEntry.class));

            when(mockDatabase.getAll()).thenReturn(bigList);
            CountDownLatch latch = new CountDownLatch(1);

            service.getAllAsync(list -> {
                assertEquals(1000, list.size());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Test
    void getFunFactAsync_shouldReturnString() throws InterruptedException {
        when(mockDatabase.getFunFact(1)).thenReturn("Fact Check");
        CountDownLatch latch = new CountDownLatch(1);

        service.getFunFactAsync(1, fact -> {
            assertEquals("Fact Check", fact);
            latch.countDown();
        }, e -> fail());

        assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
    }

    @Test
    void async_dbFailure_shouldHandleGracefully() throws InterruptedException {
        when(mockDatabase.getAll()).thenThrow(new RuntimeException("DB Connection Lost"));
        CountDownLatch latch = new CountDownLatch(1);

        service.getAllAsync(
                res -> fail("Success callback triggered on failure"),
                e -> {
                    assertNotNull(e);
                    latch.countDown();
                }
        );

        assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
    }
}