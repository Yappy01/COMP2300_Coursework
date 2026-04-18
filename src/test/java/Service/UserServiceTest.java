package Service;

import DBHandling.UserRepository;
import Models.User;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.SQLException;
import java.util.ArrayList;
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
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // already started
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        service = new UserService();

        var field = UserService.class.getDeclaredField("userRepository");
        field.setAccessible(true);
        field.set(service, mockRepo);
    }

    // =====================================================
    // 🟩 searchByUsername
    // =====================================================

    @Test
    void searchByUsername_valid_shouldReturnUser() {
        when(mockRepo.getUser("john")).thenReturn(mockUser);
        assertNotNull(service.searchByUsername("john"));
    }

    @Test
    void searchByUsername_null_shouldReturnNull() {
        when(mockRepo.getUser(null)).thenReturn(null);
        assertNull(service.searchByUsername(null));
    }

    @Test
    void searchByUsername_empty_shouldReturnNull() {
        when(mockRepo.getUser("")).thenReturn(null);
        assertNull(service.searchByUsername(""));
    }

    // =====================================================
    // 🟩 getUserName
    // =====================================================

    @Test
    void getUserName_valid_shouldReturnName() throws InterruptedException {
        when(mockRepo.getUserName(1)).thenReturn("John");

        CountDownLatch latch = new CountDownLatch(1);

        service.getUserName(1,
                result -> {
                    assertEquals("John", result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void getUserName_invalid_shouldReturnNull() throws InterruptedException {
        when(mockRepo.getUserName(-1)).thenReturn(null);

        CountDownLatch latch = new CountDownLatch(1);

        service.getUserName(-1,
                result -> {
                    assertNull(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 secureLoginAsync
    // =====================================================

    @Test
    void secureLogin_valid_shouldSucceed() throws Exception {
        when(mockRepo.secureLogin("user", "pass")).thenReturn(true);

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
    void secureLogin_invalid_shouldReturnFalse() throws Exception {
        when(mockRepo.secureLogin("user", "wrong")).thenReturn(false);

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
    void secureLogin_null_shouldFail() throws Exception {
        when(mockRepo.secureLogin(null, null)).thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.secureLoginAsync(null, null,
                r -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 deleteUserAsync
    // =====================================================

    @Test
    void deleteUser_valid_shouldSucceed() throws InterruptedException {
        when(mockRepo.deleteUser(mockUser)).thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.deleteUserAsync(mockUser,
                result -> {
                    assertTrue(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void deleteUser_null_shouldFail() throws InterruptedException {
        when(mockRepo.deleteUser(null)).thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.deleteUserAsync(null,
                r -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 updateUserAsync
    // =====================================================

    @Test
    void updateUser_valid_shouldSucceed() throws InterruptedException {
        when(mockRepo.updateUser(mockUser)).thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.updateUserAsync(mockUser,
                result -> {
                    assertTrue(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void updateUser_failure_shouldTriggerOnFailed() throws InterruptedException {
        when(mockRepo.updateUser(any())).thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.updateUserAsync(mockUser,
                r -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 fetch_notetoselfAsync
    // =====================================================

    @Test
    void fetchNote_valid_shouldReturnNote() throws InterruptedException, SQLException, ClassNotFoundException {
        when(mockRepo.fetch_notetoself("user")).thenReturn("note");

        CountDownLatch latch = new CountDownLatch(1);

        service.fetch_notetoselfAsync("user",
                result -> {
                    assertEquals("note", result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 checkUserExist / checkEmailExist
    // =====================================================

    @Test
    void checkUserExist_valid() {
        when(mockRepo.checkUserExist("user")).thenReturn(true);
        assertTrue(service.checkUserExist("user"));
    }

    @Test
    void checkEmailExist_invalid() {
        when(mockRepo.checkEmailExist("email")).thenReturn(false);
        assertFalse(service.checkEmailExist("email"));
    }

    // =====================================================
    // 🟩 validateCredentials
    // =====================================================

    @Test
    void validateCredentials_valid_shouldReturnTrue() {
        when(mockRepo.checkUserExist("user")).thenReturn(false);
        when(mockRepo.checkEmailExist("email")).thenReturn(false);

        assertEquals("", service.validateCredentials("user", "password123", "email", "ans", "q"));
    }

    @Test
    void validateCredentials_empty_shouldReturnFalse() {
        assertEquals("Please fill all blank fields", service.validateCredentials("", "pass", "email", "ans", "q"));
    }

    @Test
    void validateCredentials_usernameExists_shouldReturnFalse() {
        when(mockRepo.checkUserExist("user")).thenReturn(true);
        assertEquals("username already exist. Please choose another username", service.validateCredentials("user", "password123", "email", "ans", "q"));
    }

    @Test
    void validateCredentials_emailExists_shouldReturnFalse() {
        when(mockRepo.checkUserExist("user")).thenReturn(false);
        when(mockRepo.checkEmailExist("email")).thenReturn(true);

        assertEquals("email already exist. Please login using this email", service.validateCredentials("user", "password123", "email", "ans", "q"));
    }

    @Test
    void validateCredentials_shortPassword_shouldReturnFalse() {
        when(mockRepo.checkUserExist("user")).thenReturn(false);
        when(mockRepo.checkEmailExist("email")).thenReturn(false);

        assertEquals("Invalid Password, at least 8 characters are needed", service.validateCredentials("user", "short", "email", "ans", "q"));
    }

    // =====================================================
    // 🟩 register_userAsync
    // =====================================================

    @Test
    void registerUser_valid_shouldSucceed() throws Exception {
        when(mockRepo.register_user(mockUser)).thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.register_userAsync(mockUser,
                result -> {
                    assertTrue(result);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 getAllUserAsync
    // =====================================================

    @Test
    void getAllUsers_largeDataset_shouldHandle() throws InterruptedException {
        ArrayList<User> list = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            list.add(mock(User.class));
        }

        when(mockRepo.getAllUser()).thenReturn(list);

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllUserAsync(
                result -> {
                    assertEquals(5000, result.size());
                    latch.countDown();
                },
                e -> fail()
        );

        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 getUserFullProfileAsync
    // =====================================================

    @Test
    void getUserProfile_large_shouldHandle() throws InterruptedException {
        Map<String, String> profile = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            profile.put("k" + i, "v" + i);
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
    // 🟩 change_password
    // =====================================================

    @Test
    void changePassword_valid_shouldReturnTrue() throws Exception {
        when(mockRepo.change_password("user", "pass")).thenReturn(true);
        assertTrue(service.change_password("user", "pass"));
    }

    @Test
    void changePassword_exception_shouldThrowRuntime() throws Exception {
        when(mockRepo.change_password(any(), any())).thenThrow(new SQLException());
        assertThrows(RuntimeException.class, () ->
                service.change_password("user", "pass"));
    }

    // =====================================================
    // 🟩 Async Edge Case
    // =====================================================

    @Test
    void nullCallbacks_shouldNotCrash() throws Exception {
        when(mockRepo.register_user(any())).thenReturn(true);

        assertDoesNotThrow(() ->
                service.register_userAsync(mockUser, null, null)
        );
    }
}