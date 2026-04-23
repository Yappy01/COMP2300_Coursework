package Service;

import DBHandling.ReportDatabase;
import Models.Post;
import Models.Report;
import Models.User;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.*;
import utils.Session;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServicePartitionTest {

    private ReportService service;

    @Mock private ReportDatabase mockDatabase;
    @Mock private Post mockPost;
    @Mock private Report mockReport;
    @Mock private User mockUser;

    private static final int ASYNC_TIMEOUT = 3;

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {}
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        service = new ReportService();

        var field = ReportService.class.getDeclaredField("reportDatabase");
        field.setAccessible(true);
        field.set(service, mockDatabase);

        Session.startSession(mockUser);
        when(mockUser.getUserId()).thenReturn(1);
        when(mockPost.getPostId()).thenReturn(100);
        when(mockReport.getReportId()).thenReturn(500);
    }

    @AfterEach
    void tearDown() {
        Session.endSession();
    }

    @Nested
    class ReportPartitionTests {
        @Test
        void reportPost_emptyReason_shouldPassToDatabase() throws InterruptedException {
            when(mockDatabase.toggleReport(eq(100), eq(1), eq(""), any())).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);

            service.reportPost(mockPost, "", res -> {
                assertTrue(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void reportPost_nullPost_shouldTriggerError() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);

            service.reportPost(null, "Spam", res -> fail(), e -> {
                assertTrue(e instanceof NullPointerException);
                latch.countDown();
            });

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void reportPost_sessionTimeout_shouldFailGracefully() throws InterruptedException {
            Session.endSession();
            CountDownLatch latch = new CountDownLatch(1);

            service.reportPost(mockPost, "Reason", res -> fail(), e -> {
                assertTrue(e instanceof NullPointerException);
                latch.countDown();
            });

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class ResolutionPartitionTests {
        @Test
        void resolvePostAsync_nullResolutionDetails_shouldProceed() throws InterruptedException {
            when(mockDatabase.resolveReport(500, null)).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);

            service.resolvePostAsync(mockReport, null, res -> {
                assertTrue(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void resolvePostAsync_extremeStringLength_shouldHandle() throws InterruptedException {
            String longDetail = "A".repeat(10000);
            when(mockDatabase.resolveReport(500, longDetail)).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);

            service.resolvePostAsync(mockReport, longDetail, res -> {
                assertTrue(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class LimitTests {
        @Test
        void getAllReportAsync_emptyDatabase_shouldReturnEmptyList() throws InterruptedException {
            when(mockDatabase.getAllReports()).thenReturn(new ArrayList<>());
            CountDownLatch latch = new CountDownLatch(1);

            service.getAllReportAsync(res -> {
                assertNotNull(res);
                assertTrue(res.isEmpty());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void getAllReportAsync_veryLargeDataset_shouldNotTimeout() throws InterruptedException {
            ArrayList<Report> largeList = new ArrayList<>();
            for (int i = 0; i < 10000; i++) largeList.add(mock(Report.class));
            when(mockDatabase.getAllReports()).thenReturn(largeList);
            CountDownLatch latch = new CountDownLatch(1);

            service.getAllReportAsync(res -> {
                assertEquals(10000, res.size());
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(5, TimeUnit.SECONDS));
        }

        @Test
        void executor_concurrencyLimit_shouldHandleMultipleRequests() throws InterruptedException {
            int requestCount = 20;
            CountDownLatch latch = new CountDownLatch(requestCount);
            when(mockDatabase.softDelete(anyInt())).thenReturn(true);

            for (int i = 0; i < requestCount; i++) {
                service.deleteReportAsync(mockReport, res -> latch.countDown(), e -> latch.countDown());
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Nested
    class ErrorHandlingPartitions {
        @Test
        void database_connectionLost_shouldTriggerOnFailed() throws InterruptedException {
            when(mockDatabase.getAllReports()).thenThrow(new RuntimeException("Connection Timeout"));
            CountDownLatch latch = new CountDownLatch(1);

            service.getAllReportAsync(res -> fail(), e -> {
                assertEquals("Connection Timeout", e.getMessage());
                latch.countDown();
            });

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void deleteReportAsync_invalidId_shouldReturnFalse() throws InterruptedException {
            when(mockReport.getReportId()).thenReturn(-1);
            when(mockDatabase.softDelete(-1)).thenReturn(false);
            CountDownLatch latch = new CountDownLatch(1);

            service.deleteReportAsync(mockReport, res -> {
                assertFalse(res);
                latch.countDown();
            }, e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT, TimeUnit.SECONDS));
        }
    }
}