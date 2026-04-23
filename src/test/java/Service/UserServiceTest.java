package Service;

import DBHandling.UserRepository;
import Models.User;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    private static final int TIMEOUT = 2;

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
        service = new UserService();
        var field = UserService.class.getDeclaredField("userRepository");
        field.setAccessible(true);
        field.set(service, mockRepo);
    }

    @Nested
    class SearchAndRetrievalTests {
        @Test
        void searchByUsername_valid_shouldReturnUser() {
            when(mockRepo.getUser("john")).thenReturn(mockUser);
            assertNotNull(service.searchByUsername("john"));
        }

        @Test
        void searchByUsername_nullOrEmpty_shouldReturnNull() {
            when(mockRepo.getUser(null)).thenReturn(null);
            when(mockRepo.getUser("")).thenReturn(null);
            assertNull(service.searchByUsername(null));
            assertNull(service.searchByUsername(""));
        }

        @Test
        void getUserName_valid_shouldReturnName() throws InterruptedException {
            when(mockRepo.getUserName(1)).thenReturn("John");
            CountDownLatch latch = new CountDownLatch(1);
            service.getUserName(1, result -> {
                assertEquals("John", result);
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void fetchNote_valid_shouldReturnNote() throws Exception {
            when(mockRepo.fetch_notetoself("user")).thenReturn("Secret Note");
            CountDownLatch latch = new CountDownLatch(1);
            service.fetch_notetoselfAsync("user", result -> {
                assertEquals("Secret Note", result);
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class AuthenticationAndRegistrationTests {
        @Test
        void secureLogin_valid_shouldSucceed() throws Exception {
            when(mockRepo.secureLogin("user", "pass")).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);
            service.secureLoginAsync("user", "pass", result -> {
                assertTrue(result);
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void registerUser_valid_shouldSucceed() throws Exception {
            when(mockRepo.register_user(mockUser)).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);
            service.register_userAsync(mockUser, result -> {
                assertTrue(result);
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void check_userAsync_valid_shouldSucceed() throws Exception {
            when(mockRepo.check_user("user", "email", "ans")).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);
            service.check_userAsync("user", "email", "ans", result -> {
                assertTrue(result);
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class ValidationTests {
        @Test
        void validateCredentials_emptyFields_shouldReturnError() {
            assertEquals("Please fill all blank fields", service.validateCredentials("", "", "", "", ""));
        }

        @Test
        void validateCredentials_usernameExists_shouldReturnError() {
            when(mockRepo.checkUserExist("user")).thenReturn(true);
            assertEquals("username already exist. Please choose another username",
                    service.validateCredentials("user", "pass12345", "email@test.com", "ans", "q"));
        }

        @Test
        void validateCredentials_emailExists_shouldReturnError() {
            when(mockRepo.checkUserExist("user")).thenReturn(false);
            when(mockRepo.checkEmailExist("email@test.com")).thenReturn(true);
            assertEquals("email already exist. Please login using this email",
                    service.validateCredentials("user", "pass12345", "email@test.com", "ans", "q"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"123", "short", "1234567"})
        void validateCredentials_shortPassword_shouldReturnError(String pass) {
            assertEquals("Invalid Password, at least 8 characters are needed",
                    service.validateCredentials("user", pass, "email@test.com", "ans", "q"));
        }
    }

    @Nested
    class ProfileManagementTests {
        @Test
        void change_personalInformationAsync_valid_shouldSucceed() throws InterruptedException, SQLException, ClassNotFoundException {
            when(mockRepo.change_personalInformation(eq(1), anyString(), anyString(), anyString())).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);
            service.change_personalInformationAsync(1, "123", "01/01/2000", "M", result -> {
                assertTrue(result);
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void change_anamnesisAsync_valid_shouldSucceed() throws InterruptedException, ClassNotFoundException, SQLException{
            when(mockRepo.change_anamnesis(eq(1), any(), any(), any(), any())).thenReturn(true);
            CountDownLatch latch = new CountDownLatch(1);
            service.change_anamnesisAsync(1, "None", "None", "O+", "None", result -> {
                assertTrue(result);
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void updateUser_failure_shouldTriggerOnFailed() throws InterruptedException {
            when(mockRepo.updateUser(any())).thenThrow(new RuntimeException("DB Error"));
            CountDownLatch latch = new CountDownLatch(1);
            service.updateUserAsync(mockUser, r -> fail(), e -> latch.countDown());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }
    }

    @Nested
    class BulkDataAndSystemTests {
        @Test
        void getAllUsers_largeDataset_shouldHandle() throws InterruptedException {
            ArrayList<User> list = new ArrayList<>();
            for (int i = 0; i < 5000; i++) list.add(mock(User.class));
            when(mockRepo.getAllUser()).thenReturn(list);
            CountDownLatch latch = new CountDownLatch(1);
            service.getAllUserAsync(result -> {
                assertEquals(5000, result.size());
                latch.countDown();
            }, e -> fail());
            assertTrue(latch.await(TIMEOUT, TimeUnit.SECONDS));
        }

        @Test
        void changePassword_exception_shouldThrowRuntime() throws Exception {
            when(mockRepo.change_password(any(), any())).thenThrow(new SQLException());
            assertThrows(RuntimeException.class, () -> service.change_password("user", "pass"));
        }

        @Test
        void nullCallbacks_shouldNotCrash() {
            when(mockRepo.deleteUser(any())).thenReturn(true);
            assertDoesNotThrow(() -> service.deleteUserAsync(mockUser, null, null));
        }
    }
}