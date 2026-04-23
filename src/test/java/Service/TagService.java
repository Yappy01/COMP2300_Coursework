package Service;

import DBHandling.TagDatabase;
import Models.Tag;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServicePartitionTest {

    private TagService service;

    @Mock
    private TagDatabase mockDatabase;

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
        service = new TagService();

        // Get the private field
        var field = TagService.class.getDeclaredField("tagDatabase");

        // Make it accessible
        field.setAccessible(true);

        // Set the field on the 'service' instance with the 'mockDatabase' object
        field.set(service, mockDatabase);
    }

    @Nested
    class ContentPartitionTests {

        @Test
        void insertTagsAsync_emptyList_shouldReturnTrueImmediately() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);
            ArrayList<Tag> emptyList = new ArrayList<>();

            service.insertTagsAsync(1, emptyList, res -> {
                assertTrue(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
            verifyNoInteractions(mockDatabase);
        }

        @Test
        void insertTagsAsync_partialFailure_shouldReturnFalse() throws InterruptedException {
            Tag tag1 = mock(Tag.class);
            Tag tag2 = mock(Tag.class);
            ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(tag1, tag2));

            when(mockDatabase.insertTags(eq(1), eq(tag1))).thenReturn(true);
            when(mockDatabase.insertTags(eq(1), eq(tag2))).thenReturn(false);

            CountDownLatch latch = new CountDownLatch(1);

            service.insertTagsAsync(1, tags, res -> {
                assertFalse(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class BoundaryAndLimitTests {

        @Test
        void insertTagsAsync_largeCollection_shouldHandleLoop() throws InterruptedException {
            ArrayList<Tag> largeList = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                largeList.add(mock(Tag.class));
            }

            when(mockDatabase.insertTags(anyInt(), any(Tag.class))).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);

            service.insertTagsAsync(1, largeList, res -> {
                assertTrue(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
            verify(mockDatabase, times(100)).insertTags(eq(1), any(Tag.class));
        }

        @Test
        void insertTagsAsync_invalidPostId_shouldPassToDatabase() throws InterruptedException {
            ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(mock(Tag.class)));
            when(mockDatabase.insertTags(eq(-1), any(Tag.class))).thenReturn(false);

            CountDownLatch latch = new CountDownLatch(1);

            service.insertTagsAsync(-1, tags, res -> {
                assertFalse(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class ErrorHandlingTests {

        @Test
        void insertTagsAsync_databaseException_shouldTriggerOnFailed() throws InterruptedException {
            ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(mock(Tag.class)));
            when(mockDatabase.insertTags(anyInt(), any())).thenThrow(new RuntimeException("DB Connection Lost"));

            CountDownLatch latch = new CountDownLatch(1);

            service.insertTagsAsync(1, tags, res -> fail(), e -> {
                assertEquals("DB Connection Lost", e.getMessage());
                latch.countDown();
            });

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void insertTagsAsync_nullCallbacks_shouldNotCrash() {
            ArrayList<Tag> tags = new ArrayList<>(Arrays.asList(mock(Tag.class)));
            when(mockDatabase.insertTags(anyInt(), any())).thenReturn(true);

            assertDoesNotThrow(() -> service.insertTagsAsync(1, tags, null, null));
        }
    }
}