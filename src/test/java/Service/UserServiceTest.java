package Service;

import DBHandling.UserRepository;
import Models.User;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService service;

    @Mock
    private UserRepository mockRepo;

    @Mock
    private User mockUser;

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

        service = new UserService();

        // Inject mock repository (reflection)
        var field = UserService.class.getDeclaredField("userRepository");
        field.setAccessible(true);
        field.set(service, mockRepo);
    }

    // =====================================================
    // 🟩 searchByUsername — Boundary + Partition
    // =====================================================

    @Test
    void searchByUsername_valid_shouldReturnUser() {
        when(mockRepo.getUser("john")).thenReturn(mockUser);

        User result = service.searchByUsername("john");

        assertNotNull(result);
    }

    @Test
    void searchByUsername_null_shouldReturnNull() {
        when(mockRepo.getUser(null)).thenReturn(null);

        User result = service.searchByUsername(null);

        assertNull(result);
    }

    @Test
    void searchByUsername_empty_shouldReturnNull() {
        when(mockRepo.getUser("")).thenReturn(null);

        assertNull(service.searchByUsername(""));
    }

    // =====================================================
    // 🟩 getUserName — Boundary
    // =====================================================

    @Test
    void getUserName_valid_shouldReturnName() {
        when(mockRepo.getUserName(1)).thenReturn("John");

        assertEquals("John", service.getUserName(1));
    }

    @Test
    void getUserName_invalidId_shouldReturnNull() {
        when(mockRepo.getUserName(-1)).thenReturn(null);

        assertNull(service.getUserName(-1));
    }

    // =====================================================
    // 🟩 secureLoginAsync — Partition + Boundary
    // =====================================================

    @Test
    void secureLogin_valid_shouldSucceed() throws InterruptedException {
        try {
            when(mockRepo.secureLogin("user", "pass")).thenReturn(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        CountDownLatch latch = new CountDownLatch(1);

        service.secureLoginAsync("user", "pass",
                result -> {
                    assertTrue(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void secureLogin_invalid_shouldReturnFalse() throws InterruptedException {
        try {
            when(mockRepo.secureLogin("user", "wrong")).thenReturn(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        CountDownLatch latch = new CountDownLatch(1);

        service.secureLoginAsync("user", "wrong",
                result -> {
                    assertFalse(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void secureLogin_nullInput_shouldFail() throws InterruptedException {
        try {
            when(mockRepo.secureLogin(null, null))
                    .thenThrow(new RuntimeException());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        CountDownLatch latch = new CountDownLatch(1);

        service.secureLoginAsync(null, null,
                r -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 change_password — Exception Handling
    // =====================================================

    @Test
    void changePassword_valid_shouldReturnTrue() throws Exception {
        when(mockRepo.change_password("user", "pass")).thenReturn(true);

        assertTrue(service.change_password("user", "pass"));
    }

    @Test
    void changePassword_sqlException_shouldThrowRuntime() throws Exception {
        when(mockRepo.change_password(any(), any()))
                .thenThrow(new SQLException());

        assertThrows(RuntimeException.class, () ->
                service.change_password("user", "pass"));
    }

    // =====================================================
    // 🟩 register_userAsync — Boundary + Partition
    // =====================================================

    @Test
    void registerUser_valid_shouldSucceed() throws InterruptedException {
        try {
            when(mockRepo.register_user(mockUser)).thenReturn(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        CountDownLatch latch = new CountDownLatch(1);

        service.register_userAsync(mockUser,
                result -> {
                    assertTrue(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void registerUser_null_shouldFail() throws InterruptedException {
        try {
            when(mockRepo.register_user(null))
                    .thenThrow(new RuntimeException());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        CountDownLatch latch = new CountDownLatch(1);

        service.register_userAsync(null,
                r -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 checkUserExist / checkEmailExist — Partition
    // =====================================================

    @Test
    void checkUserExist_valid() {
        when(mockRepo.checkUserExist("user")).thenReturn(true);

        assertTrue(service.checkUserExist("user"));
    }

    @Test
    void checkEmailExist_invalid() {
        when(mockRepo.checkEmailExist("bad@email")).thenReturn(false);

        assertFalse(service.checkEmailExist("bad@email"));
    }

    // =====================================================
    // 🟩 getUserFullProfileAsync — Limit + Async
    // =====================================================

    @Test
    void getUserProfile_largeData_shouldHandle() throws InterruptedException {
        Map<String, String> profile = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            profile.put("key" + i, "value" + i);
        }

        when(mockRepo.getUserFullProfile(1)).thenReturn(profile);

        CountDownLatch latch = new CountDownLatch(1);

        service.getUserFullProfileAsync(1,
                result -> {
                    assertEquals(1000, result.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 change_notetoselfAsync — Boundary
    // =====================================================

    @Test
    void changeNote_valid_shouldSucceed() throws InterruptedException {
        try {
            when(mockRepo.change_notetoself("user", "note")).thenReturn(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        CountDownLatch latch = new CountDownLatch(1);

        service.change_notetoselfAsync("user", "note",
                result -> {
                    assertTrue(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 Async Edge Case
    // =====================================================

    @Test
    void nullCallbacks_shouldNotCrash() {
        try {
            when(mockRepo.register_user(any())).thenReturn(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        assertDoesNotThrow(() ->
                service.register_userAsync(mockUser, null, null)
        );
    }
}