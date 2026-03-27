package Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import Models.Post;
import Models.User;
import Models.Comment;
import utils.Session;
import DBHandling.ComPostDatabase;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.util.ArrayList;

class PostServiceTest {

    private Post mockPost;
    private User mockUser;
    private MockedStatic<ComPostDatabase> mockedDatabase;

    @BeforeEach
    void setUp() {
        // 1. Create fake objects
        mockPost = mock(Post.class);
        mockUser = mock(User.class);

        // 2. Setup mock behavior
        when(mockPost.getPostId()).thenReturn(101);
        when(mockPost.getUserId()).thenReturn(202); // The post creator
        when(mockUser.getUserId()).thenReturn(5);   // The person liking/commenting

        // 3. Start a "Fake" Session
        Session.startSession(mockUser);

        // 4. Mock the Static Database class to intercept calls
        mockedDatabase = mockStatic(ComPostDatabase.class);
    }

    @AfterEach
    void tearDown() {
        // Crucial for MockedStatic: must close after each test
        mockedDatabase.close();
    }

    // --- 1. FUNCTIONAL / UNIT TESTING ---

    @Test
    void testLikePostCallsCorrectDatabaseParams() {
        PostService.likePost(mockPost);

        // Verify the service pulled the correct IDs: 101 from Post, 5 from Session
        mockedDatabase.verify(() ->
                ComPostDatabase.toggleLike(101, 5)
        );
    }

    @Test
    void testCommentPostSendsCorrectData() {
        String content = "Hello World";
        PostService.commentPost(mockPost, content);

        // Verify service uses the post's ID and content
        mockedDatabase.verify(() ->
                ComPostDatabase.addComment(eq(101), anyInt(), eq(content))
        );
    }

    // --- 2. BOUNDARY TESTING ---

    @Test
    void testCommentPostWithEmptyString() {
        // Boundary: Does the service handle empty comments?
        assertDoesNotThrow(() -> PostService.commentPost(mockPost, ""));

        mockedDatabase.verify(() ->
                ComPostDatabase.addComment(101, 202, "")
        );
    }

    @Test
    @DisplayName("Boundary: Handle extremely long comment text")
    void testCommentPostWithHugeContent() {
        String longContent = "A".repeat(5000);
        assertDoesNotThrow(() -> PostService.commentPost(mockPost, longContent));
    }

    @Test
    void testGetCommentsWithNullPost() {
        // Boundary: Passing null instead of a Post object
        ArrayList<Comment> result = PostService.getComments(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- 3. LIMIT TESTING ---

    @Test
    void testGetAllPostsRespectsInternalLimit() {
        PostService.getAllPosts("recent");

        // Limit Test: Verify the hardcoded '12' is actually passed to the DB
        mockedDatabase.verify(() ->
                ComPostDatabase.getCard(12, "recent")
        );
    }

    @Test
    void testGetCommentsLimitParameters() {
        PostService.getComments(mockPost);

        // Limit Test: Verify the hardcoded '12' is used for fetching comments
        mockedDatabase.verify(() ->
                ComPostDatabase.getComment(101, 12)
        );
    }

    // --- 4. ERROR HANDLING (Negative Testing) ---

    @Test
    void testGetCommentsWhenDatabaseFails() {
        // Simulate the database throwing an error
        mockedDatabase.when(() -> ComPostDatabase.getComment(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Connection Timeout"));

        // The service has a try-catch, so it shouldn't crash
        ArrayList<Comment> result = PostService.getComments(mockPost);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list on DB failure");
    }
}