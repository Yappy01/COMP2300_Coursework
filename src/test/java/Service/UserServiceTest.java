package Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import Models.User;
import DBHandling.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

class UserServiceTest {

    private UserService userService;
    private MockedStatic<UserRepository> mockedRepo;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        mockedRepo = mockStatic(UserRepository.class);
    }

    @AfterEach
    void tearDown() {
        mockedRepo.close();
    }

    // --- 1. UNIT TESTING (HAPPY PATH) ---

    @Test
    @DisplayName("Unit: Should return user when username exists")
    void testSearchByUsernameFound() {
        User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("java_expert");

        mockedRepo.when(() -> UserRepository.getUser("java_expert"))
                .thenReturn(mockUser);

        User result = userService.searchByUsername("java_expert");

        assertNotNull(result);
        assertEquals("java_expert", result.getName());
    }

    // --- 2. PARTITION TESTING (COMPLETED) ---

    @Test
    @DisplayName("Partition: Username not found")
    void testSearchByUsernameNotFound() {
        mockedRepo.when(() -> UserRepository.getUser("non_existent"))
                .thenReturn(null);

        User result = userService.searchByUsername("non_existent");

        assertNull(result);
    }

    @Test
    @DisplayName("Partition: Empty username")
    void testSearchByEmptyString() {
        mockedRepo.when(() -> UserRepository.getUser(""))
                .thenReturn(null);

        User result = userService.searchByUsername("");

        assertNull(result);
        mockedRepo.verify(() -> UserRepository.getUser(""));
    }

    @Test
    @DisplayName("Partition: Null username")
    void testSearchByNullKeyword() {
        mockedRepo.when(() -> UserRepository.getUser(null))
                .thenReturn(null);

        assertDoesNotThrow(() -> userService.searchByUsername(null));

        mockedRepo.verify(() -> UserRepository.getUser(null));
    }

    @Test
    @DisplayName("Partition: Special characters input")
    void testSearchBySpecialCharacters() {
        String input = "admin'--";

        mockedRepo.when(() -> UserRepository.getUser(input))
                .thenReturn(null);

        User result = userService.searchByUsername(input);

        assertNull(result);
        mockedRepo.verify(() -> UserRepository.getUser(input));
    }

    @Test
    @DisplayName("Partition: Case sensitivity check")
    void testSearchByCaseSensitivity() {
        mockedRepo.when(() -> UserRepository.getUser("Java_Expert"))
                .thenReturn(null);

        User result = userService.searchByUsername("Java_Expert");

        assertNull(result);
    }

    @Test
    @DisplayName("Partition: Leading/trailing spaces")
    void testSearchByWhitespaceInput() {
        mockedRepo.when(() -> UserRepository.getUser(" java_expert "))
                .thenReturn(null);

        User result = userService.searchByUsername(" java_expert ");

        assertNull(result);
    }

    // --- 3. LIMIT TESTING (ADDED) ---

    @Test
    @DisplayName("Limit: Very long username input")
    void testSearchByVeryLongUsername() {
        String longInput = "a".repeat(10000);

        mockedRepo.when(() -> UserRepository.getUser(longInput))
                .thenReturn(null);

        User result = userService.searchByUsername(longInput);

        assertNull(result);
    }

    @Test
    @DisplayName("Limit: Single character username")
    void testSearchBySingleCharacter() {
        mockedRepo.when(() -> UserRepository.getUser("a"))
                .thenReturn(null);

        User result = userService.searchByUsername("a");

        assertNull(result);
    }

    // --- 4. NEGATIVE / ERROR HANDLING ---

    @Test
    @DisplayName("Error: Database failure handling")
    void testSearchByUsernameDatabaseError() {
        mockedRepo.when(() -> UserRepository.getUser(anyString()))
                .thenThrow(new RuntimeException("Database Connection Failed"));

        assertThrows(RuntimeException.class,
                () -> userService.searchByUsername("anyUser"));
    }
}