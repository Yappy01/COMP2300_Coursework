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
        // Intercept all static calls to UserRepository
        mockedRepo = mockStatic(UserRepository.class);
    }

    @AfterEach
    void tearDown() {
        // Clean up the static mock to avoid affecting other tests
        mockedRepo.close();
    }

    // --- 1. UNIT TESTING (The "Happy Path") ---

    @Test
    @DisplayName("Unit: Should return a user object when the username exists")
    void testSearchByUsernameFound() {
        // We mock the User to avoid filling out the constructor (password, email, etc.)
        User mockUser = mock(User.class);
        when(mockUser.getName()).thenReturn("java_expert");

        // Stub the Repository to return our mock user
        mockedRepo.when(() -> UserRepository.getUser("java_expert")).thenReturn(mockUser);

        User result = userService.searchByUsername("java_expert");

        assertNotNull(result, "Result should not be null for an existing user");
        assertEquals("java_expert", result.getName(), "The returned username should match the search");
    }

    // --- 2. NEGATIVE TESTING (Data Missing) ---

    @Test
    @DisplayName("Negative: Should return null when the repository finds nothing")
    void testSearchByUsernameNotFound() {
        // Stub the Repository to return null
        mockedRepo.when(() -> UserRepository.getUser("non_existent")).thenReturn(null);

        User result = userService.searchByUsername("non_existent");

        assertNull(result, "Service should return null if user is not in the database");
    }

    // --- 3. BOUNDARY TESTING (Input Edges) ---

    @Test
    @DisplayName("Boundary: Handle an empty string search")
    void testSearchByEmptyString() {
        userService.searchByUsername("");

        // Verify the service passes the empty string exactly as is to the repo
        mockedRepo.verify(() -> UserRepository.getUser(""));
    }

    @Test
    @DisplayName("Boundary: Handle a null search keyword")
    void testSearchByNullKeyword() {
        // This checks if your service or repo crashes on null
        assertDoesNotThrow(() -> userService.searchByUsername(null));

        mockedRepo.verify(() -> UserRepository.getUser(null));
    }

    @Test
    @DisplayName("Boundary: Handle SQL-like characters (Security/Sanitization)")
    void testSearchBySpecialCharacters() {
        String specialInput = "admin'--";
        userService.searchByUsername(specialInput);

        // Ensures the service doesn't strip characters before they reach the repo/DB
        mockedRepo.verify(() -> UserRepository.getUser(specialInput));
    }

    // --- 4. ERROR HANDLING ---

    @Test
    @DisplayName("Error: Should handle database exceptions gracefully")
    void testSearchByUsernameDatabaseError() {
        // Simulate a database connection failure
        mockedRepo.when(() -> UserRepository.getUser(anyString()))
                .thenThrow(new RuntimeException("Database Connection Failed"));

        // If your service doesn't have a try-catch, this will throw the exception.
        // This test helps you see if you need to add error handling in UserService.
        assertThrows(RuntimeException.class, () -> userService.searchByUsername("anyUser"));
    }
}