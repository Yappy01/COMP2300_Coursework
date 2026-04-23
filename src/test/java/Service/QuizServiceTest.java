package Service;

import DBHandling.QuizDatabase;
import Models.Quiz;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizServicePartitionTest {

    private QuizService service;

    @Mock
    private QuizDatabase mockDatabase;

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
        service = new QuizService();

        var field = QuizService.class.getDeclaredField("database");
        field.setAccessible(true);
        field.set(service, mockDatabase);
    }

    @Nested
    class QuizTypePartitions {

        @Test
        void getQuizzesAsync_validTypeId_shouldReturnList() throws InterruptedException {
            List<Quiz> quizzes = List.of(mock(Quiz.class), mock(Quiz.class));
            when(mockDatabase.getQuizzesByTypeId(1)).thenReturn(quizzes);
            CountDownLatch latch = new CountDownLatch(1);

            service.getQuizzesAsync(1, res -> {
                assertEquals(2, res.size());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void getQuizzesAsync_nonExistentTypeId_shouldReturnEmpty() throws InterruptedException {
            when(mockDatabase.getQuizzesByTypeId(999)).thenReturn(new ArrayList<>());
            CountDownLatch latch = new CountDownLatch(1);

            service.getQuizzesAsync(999, res -> {
                assertTrue(res.isEmpty());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void getQuizzesAsync_negativeTypeId_shouldPassToDatabase() throws InterruptedException {
            when(mockDatabase.getQuizzesByTypeId(-1)).thenReturn(new ArrayList<>());
            CountDownLatch latch = new CountDownLatch(1);

            service.getQuizzesAsync(-1, res -> {
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
            verify(mockDatabase).getQuizzesByTypeId(-1);
        }
    }

    @Nested
    class LimitTests {

        @Test
        void getQuizzesAsync_largeDataset_shouldHandle() throws InterruptedException {
            List<Quiz> largeList = new ArrayList<>();
            for (int i = 0; i < 5000; i++) largeList.add(mock(Quiz.class));
            when(mockDatabase.getQuizzesByTypeId(1)).thenReturn(largeList);
            CountDownLatch latch = new CountDownLatch(1);

            service.getQuizzesAsync(1, res -> {
                assertEquals(5000, res.size());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void executor_threadPoolExhaustion_shouldQueueTasks() throws InterruptedException {
            int taskCount = 10;
            CountDownLatch latch = new CountDownLatch(taskCount);
            when(mockDatabase.getQuizzesByTypeId(anyInt())).thenReturn(new ArrayList<>());

            for (int i = 0; i < taskCount; i++) {
                service.getQuizzesAsync(1, res -> latch.countDown(), e -> latch.countDown());
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Nested
    class ErrorHandlingPartitions {

        @Test
        void getQuizzesAsync_databaseError_shouldTriggerOnFailed() throws InterruptedException {
            when(mockDatabase.getQuizzesByTypeId(anyInt())).thenThrow(new RuntimeException("Query Timeout"));
            CountDownLatch latch = new CountDownLatch(1);

            service.getQuizzesAsync(1, res -> fail(), e -> {
                assertEquals("Query Timeout", e.getMessage());
                latch.countDown();
            });

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void getQuizzesAsync_nullCallbacks_shouldNotCrash() {
            when(mockDatabase.getQuizzesByTypeId(anyInt())).thenReturn(new ArrayList<>());
            assertDoesNotThrow(() -> service.getQuizzesAsync(1, null, null));
        }
    }
}