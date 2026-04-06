package Service;

import DBHandling.EventDatabase;
import Models.UserEvent;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private EventService service;

    @Mock
    private EventDatabase mockDatabase;

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new EventService();
        service.database = mockDatabase;
    }

    // =====================================================
    // 🟩 SAVE EVENT — Boundary + Partition + Limit
    // =====================================================

    @Test
    void saveEvent_validPartition_shouldSucceed() throws InterruptedException {
        when(mockDatabase.saveEvent(any(), any(), any(), any(), any()))
                .thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync("Meeting", "Desc",
                new Timestamp(System.currentTimeMillis()),
                1, 1,
                latch::countDown,
                e -> fail());

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    // NULL partition
    @Test
    void saveEvent_nullTitle_shouldFail() throws InterruptedException {
        when(mockDatabase.saveEvent(isNull(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync(null, "Desc",
                new Timestamp(System.currentTimeMillis()),
                1, 1,
                () -> fail(),
                e -> {
                    assertNotNull(e);
                    latch.countDown();
                });

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // EMPTY boundary
    @Test
    void saveEvent_emptyTitle_shouldFail() throws InterruptedException {
        when(mockDatabase.saveEvent(eq(""), any(), any(), any(), any()))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync("", "Desc",
                new Timestamp(System.currentTimeMillis()),
                1, 1,
                () -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // 🟢 MIN boundary
    @Test
    void saveEvent_minLengthTitle_shouldPass() throws InterruptedException {
        when(mockDatabase.saveEvent(eq("A"), any(), any(), any(), any()))
                .thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync("A", "Desc",
                new Timestamp(System.currentTimeMillis()),
                1, 1,
                latch::countDown,
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // 🟢 MAX boundary
    @Test
    void saveEvent_maxLengthTitle_shouldPass() throws InterruptedException {
        String max = "A".repeat(255);

        when(mockDatabase.saveEvent(eq(max), any(), any(), any(), any()))
                .thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync(max, "Desc",
                new Timestamp(System.currentTimeMillis()),
                1, 1,
                latch::countDown,
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // 🔴 OVER LIMIT partition
    @Test
    void saveEvent_overMaxLength_shouldFail() throws InterruptedException {
        String over = "A".repeat(256);

        when(mockDatabase.saveEvent(eq(over), any(), any(), any(), any()))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync(over, "Desc",
                new Timestamp(System.currentTimeMillis()),
                1, 1,
                () -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // 🔴 USER ID partitions
    @Test
    void saveEvent_negativeUserId_shouldFail() throws InterruptedException {
        when(mockDatabase.saveEvent(any(), any(), any(), eq(-1), any()))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync("Title", "Desc",
                new Timestamp(System.currentTimeMillis()),
                -1, 1,
                () -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // 🟡 LIMIT TEST — very large description
    @Test
    void saveEvent_largeDescription_shouldHandle() throws InterruptedException {
        String largeDesc = "A".repeat(10000);

        when(mockDatabase.saveEvent(any(), eq(largeDesc), any(), any(), any()))
                .thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.saveEventAsync("Title", largeDesc,
                new Timestamp(System.currentTimeMillis()),
                1, 1,
                latch::countDown,
                e -> fail());

        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 DELETE EVENT — Partition + Boundary
    // =====================================================

    @Test
    void deleteEvent_valid_shouldPass() throws InterruptedException {
        when(mockDatabase.deleteEvent("Title", 1)).thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.deleteEventAsync("Title", 1,
                latch::countDown,
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void deleteEvent_nullTitle_shouldFail() throws InterruptedException {
        when(mockDatabase.deleteEvent(null, 1))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.deleteEventAsync(null, 1,
                () -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void deleteEvent_nonexistent_shouldReturnFalse() throws InterruptedException {
        when(mockDatabase.deleteEvent("fake", 1)).thenReturn(false);

        CountDownLatch latch = new CountDownLatch(1);

        service.deleteEventAsync("fake", 1,
                latch::countDown,
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 GET FILTERED EVENTS — Partition Testing
    // =====================================================

    @Test
    void getFilteredEvents_validPartitions() throws InterruptedException {
        when(mockDatabase.getFilteredEvents(anyInt(), anyInt(), eq("future")))
                .thenReturn(List.of());

        CountDownLatch latch = new CountDownLatch(1);

        service.getFilteredEventsAsync(1, 1, "future",
                list -> {
                    assertNotNull(list);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void getFilteredEvents_invalidTimeMode_shouldFail() throws InterruptedException {
        when(mockDatabase.getFilteredEvents(anyInt(), anyInt(), eq("invalid")))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.getFilteredEventsAsync(1, 1, "invalid",
                list -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void getFilteredEvents_nullTimeMode_shouldFail() throws InterruptedException {
        when(mockDatabase.getFilteredEvents(anyInt(), anyInt(), isNull()))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.getFilteredEventsAsync(1, 1, null,
                list -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 GET ALL EVENTS — LIMIT TESTING
    // =====================================================

    @Test
    void getAllEvents_largeDataset_shouldHandle() throws InterruptedException {
        List<UserEvent> bigList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            bigList.add(mock(UserEvent.class));
        }

        when(mockDatabase.getAllEvents()).thenReturn(bigList);

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllEventsAsync(
                list -> {
                    assertEquals(10000, list.size());
                    latch.countDown();
                },
                e -> fail()
        );

        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 ASYNC EDGE CASES
    // =====================================================

    @Test
    void nullCallbacks_shouldNotCrash() {
        when(mockDatabase.saveEvent(any(), any(), any(), any(), any()))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                service.saveEventAsync("Title", "Desc",
                        new Timestamp(System.currentTimeMillis()),
                        1, 1,
                        null, null)
        );
    }
}