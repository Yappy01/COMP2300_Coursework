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
        mockPost = mock(Post.class);
        mockUser = mock(User.class);

        when(mockPost.getPostId()).thenReturn(101);
        when(mockPost.getUserId()).thenReturn(202);
        when(mockUser.getUserId()).thenReturn(5);

        Session.startSession(mockUser);
        mockedDatabase = mockStatic(ComPostDatabase.class);
    }

    @AfterEach
    void tearDown() {
        mockedDatabase.close();
    }

    // --- 1. FUNCTIONAL / UNIT TESTING ---

    @Test
    void testLikePostCallsCorrectDatabaseParams() {
        PostService.likePost(mockPost);

        mockedDatabase.verify(() ->
                ComPostDatabase.toggleLike(101, 5)
        );
    }

    @Test
    void testCommentPostSendsCorrectData() {
        String content = "Hello World";
        PostService.commentPost(mockPost, content);

        mockedDatabase.verify(() ->
                ComPostDatabase.addComment(eq(101), anyInt(), eq(content))
        );
    }

    // --- 2. PARTITION TESTING ---

    @Test
    void testCommentPostWithEmptyString() {
        assertDoesNotThrow(() -> PostService.commentPost(mockPost, ""));

        mockedDatabase.verify(() ->
                ComPostDatabase.addComment(101, 202, "")
        );
    }

    @Test
    void testCommentPostWithLongString() {
        String longContent = "A".repeat(5000);
        assertDoesNotThrow(() -> PostService.commentPost(mockPost, longContent));
    }

    @Test
    void testCommentPostWithSpecialCharacters() {
        String content = "@#$%^&*()!";
        assertDoesNotThrow(() -> PostService.commentPost(mockPost, content));
    }

    @Test
    void testCommentPostWithNullContent() {
        assertDoesNotThrow(() ->
                PostService.commentPost(mockPost, null)
        );

        // Verify that DB is NEVER called when content is null
        mockedDatabase.verify(() ->
                        ComPostDatabase.addComment(anyInt(), anyInt(), anyString()),
                never()
        );
    }

    @Test
    void testLikePostWithNullPost() {
        assertThrows(Exception.class, () ->
                PostService.likePost(null)
        );
    }

    @Test
    void testGetCommentsWithNullPost() {
        ArrayList<Comment> result = PostService.getComments(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- 3. LIMIT TESTING ---

    @Test
    void testGetAllPostsRespectsInternalLimit() {
        PostService.getAllPosts("recent");

        mockedDatabase.verify(() ->
                ComPostDatabase.getCard(12, "recent")
        );
    }

    @Test
    void testGetCommentsLimitParameters() {
        PostService.getComments(mockPost);

        mockedDatabase.verify(() ->
                ComPostDatabase.getComment(101, 12)
        );
    }

    // --- 4. ERROR HANDLING (Negative Testing) ---

    @Test
    void testGetCommentsWhenDatabaseFails() {
        mockedDatabase.when(() -> ComPostDatabase.getComment(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Connection Timeout"));

        ArrayList<Comment> result = PostService.getComments(mockPost);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list on DB failure");
    }
}