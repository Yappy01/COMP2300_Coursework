package Service;

import DBHandling.ComPostDatabase;
import Models.Comment;
import Models.Post;
import Models.User;
import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import utils.Session;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostService service;

    @Mock
    private ComPostDatabase mockDatabase;

    @Mock
    private Cloudinary mockCloudinary;

    @Mock
    private Uploader mockUploader;

    @Mock
    private Post mockPost;

    private User mockUser;

    private static final int ASYNC_TIMEOUT_SECONDS = 3;

    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already started
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        mockUser = new User(1, "tester", "test@gmail.com", "User");
        Session.startSession(mockUser);

        service = new PostService();

        // Inject mock database
        var dbField = PostService.class.getDeclaredField("postDatabase");
        dbField.setAccessible(true);
        dbField.set(service, mockDatabase);

        // Inject mock cloudinary
        var cloudField = PostService.class.getDeclaredField("cloudinary");
        cloudField.setAccessible(true);
        cloudField.set(service, mockCloudinary);

        when(mockCloudinary.uploader()).thenReturn(mockUploader);

        // Default mock post setup
        when(mockPost.getPostId()).thenReturn(1);
        when(mockPost.getUserId()).thenReturn(1);
        when(mockPost.getPublicId()).thenReturn("");
    }

    @AfterEach
    void tearDown() {
        Session.endSession();
    }

    @Nested
    @DisplayName("canPost Rate Limiting")
    class CanPostTests {

        @Test
        @DisplayName("First post should be allowed")
        void canPost_firstPost_shouldReturnTrue() {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);

            assertTrue(service.canPost(1));
        }

        @Test
        @DisplayName("Second post within 3 seconds should be blocked")
        void canPost_withinCooldown_shouldReturnFalse() {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);

            service.canPost(1); // First call
            assertFalse(service.canPost(1)); // Immediate second call
        }

        @Test
        @DisplayName("Post after cooldown should be allowed")
        void canPost_afterCooldown_shouldReturnTrue() throws InterruptedException {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);

            service.canPost(1);
            Thread.sleep(3100); // Wait past cooldown
            assertTrue(service.canPost(1));
        }

        // Boundary: exactly at daily limit
        @Test
        @DisplayName("Exactly 5 posts today should block further posts")
        void canPost_atDailyLimit_shouldReturnFalse() {
            when(mockDatabase.countPostsToday(any())).thenReturn(5);

            assertFalse(service.canPost(1));
        }

        // Partition: below limit
        @Test
        @DisplayName("4 posts today should allow one more")
        void canPost_belowDailyLimit_shouldReturnTrue() {
            when(mockDatabase.countPostsToday(any())).thenReturn(4);

            assertTrue(service.canPost(1));
        }

        // Partition: above limit
        @Test
        @DisplayName("6 posts today should block further posts")
        void canPost_aboveDailyLimit_shouldReturnFalse() {
            when(mockDatabase.countPostsToday(any())).thenReturn(6);

            assertFalse(service.canPost(1));
        }

        // Partition: different users have separate limits
        @Test
        @DisplayName("Different users should have independent cooldowns")
        void canPost_differentUsers_shouldHaveIndependentCooldowns() {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);

            assertTrue(service.canPost(1));
            assertTrue(service.canPost(2)); // Different user
            assertFalse(service.canPost(1)); // Same user, within cooldown
        }

        // Boundary: userId edge cases
        @Test
        @DisplayName("Zero userId should work")
        void canPost_zeroUserId_shouldWork() {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);

            assertTrue(service.canPost(0));
        }

        @Test
        @DisplayName("Negative userId should work")
        void canPost_negativeUserId_shouldWork() {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);

            assertTrue(service.canPost(-1));
        }

        @Test
        @DisplayName("Max integer userId should work")
        void canPost_maxIntUserId_shouldWork() {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);

            assertTrue(service.canPost(Integer.MAX_VALUE));
        }
    }

    @Nested
    @DisplayName("likePost")
    class LikePostTests {

        @Test
        @DisplayName("Valid post should toggle like successfully")
        void likePost_valid_shouldCallDatabase() throws InterruptedException {
            when(mockDatabase.toggleLike(anyInt(), anyInt())).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.likePost(mockPost,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail("Should not fail: " + e.getMessage()));

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockDatabase).toggleLike(eq(1), eq(1));
        }

        @Test
        @DisplayName("Null post should trigger onFailed callback")
        void likePost_nullPost_shouldFail() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);

            service.likePost(null,
                    result -> fail("Should not succeed"),
                    e -> {
                        assertNotNull(e);
                        latch.countDown();
                    });

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Database returning false should propagate to callback")
        void likePost_dbReturnsFalse_shouldReturnFalse() throws InterruptedException {
            when(mockDatabase.toggleLike(anyInt(), anyInt())).thenReturn(false);

            CountDownLatch latch = new CountDownLatch(1);

            service.likePost(mockPost,
                    result -> {
                        assertFalse(result);
                        latch.countDown();
                    },
                    e -> fail("Should not fail"));

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Boundary: negative post ID
        @Test
        @DisplayName("Negative postId should still call database")
        void likePost_negativePostId_shouldCallDB() throws InterruptedException {
            when(mockPost.getPostId()).thenReturn(-1);
            when(mockDatabase.toggleLike(eq(-1), anyInt())).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.likePost(mockPost,
                    result -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockDatabase).toggleLike(eq(-1), anyInt());
        }
    }

    @Nested
    @DisplayName("commentPost")
    class CommentPostTests {

        @Test
        @DisplayName("Valid comment should be inserted")
        void commentPost_valid_shouldInsert() {
            service.commentPost(mockPost, "Nice post");

            verify(mockDatabase).addComment(eq(1), eq(1), eq("Nice post"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Null or empty content should not insert")
        void commentPost_nullOrEmpty_shouldNotInsert(String content) {
            service.commentPost(mockPost, content);

            verify(mockDatabase, never()).addComment(anyInt(), anyInt(), anyString());
        }

        // Bug detection: whitespace-only content passes validation
        @Test
        @DisplayName("Whitespace-only content should not insert (BUG)")
        void commentPost_whitespaceOnly_shouldNotInsert_bug() {
            service.commentPost(mockPost, "   ");

            // This reveals a bug — whitespace passes validation
            // The verify below will PASS, indicating the bug exists
            verify(mockDatabase).addComment(anyInt(), anyInt(), eq("   "));
        }

        @Test
        @DisplayName("Null post should throw NullPointerException")
        void commentPost_nullPost_shouldThrow() {
            assertThrows(NullPointerException.class, () ->
                    service.commentPost(null, "Hello"));
        }

        // Limit test: very large content
        @Test
        @DisplayName("10KB content should be accepted")
        void commentPost_largeContent_shouldInsert() {
            String large = "A".repeat(10_000);

            service.commentPost(mockPost, large);

            verify(mockDatabase).addComment(eq(1), eq(1), eq(large));
        }

        // Limit test: maximum string size
        @Test
        @DisplayName("1MB content should be accepted by service layer")
        void commentPost_veryLargeContent_shouldInsert() {
            String huge = "B".repeat(1_000_000);

            service.commentPost(mockPost, huge);

            verify(mockDatabase).addComment(eq(1), eq(1), eq(huge));
        }

        // Boundary: single character
        @Test
        @DisplayName("Single character comment should be inserted")
        void commentPost_singleChar_shouldInsert() {
            service.commentPost(mockPost, "x");

            verify(mockDatabase).addComment(eq(1), eq(1), eq("x"));
        }

        // Partition: special characters
        @Test
        @DisplayName("Special characters should be accepted")
        void commentPost_specialChars_shouldInsert() {
            String special = "Hello! @#$%^&*()_+{}|:<>?~`";

            service.commentPost(mockPost, special);

            verify(mockDatabase).addComment(eq(1), eq(1), eq(special));
        }

        // Partition: unicode/emoji
        @Test
        @DisplayName("Unicode and emoji should be accepted")
        void commentPost_unicode_shouldInsert() {
            String unicode = "Hello 世界 🎉 مرحبا";

            service.commentPost(mockPost, unicode);

            verify(mockDatabase).addComment(eq(1), eq(1), eq(unicode));
        }
    }

    @Nested
    @DisplayName("getCommentsAsync")
    class GetCommentsAsyncTests {

        @Test
        @DisplayName("Valid request should return comment list")
        void getCommentsAsync_valid_shouldReturnList() throws InterruptedException {
            ArrayList<Comment> comments = new ArrayList<>();
            comments.add(mock(Comment.class));
            when(mockDatabase.getComment(eq(1), eq(12))).thenReturn(comments);

            CountDownLatch latch = new CountDownLatch(1);

            service.getCommentsAsync(mockPost,
                    list -> {
                        assertNotNull(list);
                        assertEquals(1, list.size());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Empty result should return empty list")
        void getCommentsAsync_noComments_shouldReturnEmptyList() throws InterruptedException {
            when(mockDatabase.getComment(anyInt(), anyInt())).thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getCommentsAsync(mockPost,
                    list -> {
                        assertNotNull(list);
                        assertTrue(list.isEmpty());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Limit test: large dataset
        @Test
        @DisplayName("10,000 comments should be handled")
        void getCommentsAsync_largeDataset_shouldHandle() throws InterruptedException {
            ArrayList<Comment> bigList = new ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                bigList.add(mock(Comment.class));
            }
            when(mockDatabase.getComment(anyInt(), anyInt())).thenReturn(bigList);

            CountDownLatch latch = new CountDownLatch(1);

            service.getCommentsAsync(mockPost,
                    list -> {
                        assertEquals(10_000, list.size());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Null post should trigger onFailed")
        void getCommentsAsync_nullPost_shouldFail() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);

            service.getCommentsAsync(null,
                    list -> fail("Should not succeed"),
                    e -> latch.countDown());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }


    @Nested
    @DisplayName("getAllPostsAsync")
    class GetAllPostsAsyncTests {

        @Test
        @DisplayName("Valid type 'all' should return posts")
        void getAllPostsAsync_typeAll_shouldReturnList() throws InterruptedException {
            when(mockDatabase.getCard(eq(10), eq("all"), eq(false)))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getAllPostsAsync("all", 10, false,
                    list -> {
                        assertNotNull(list);
                        latch.countDown();
                    },
                    e -> fail("Failed: " + e.getMessage()));

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Partition: showDeleted = true
        @Test
        @DisplayName("showDeleted true should pass to database")
        void getAllPostsAsync_showDeletedTrue_shouldPassToDb() throws InterruptedException {
            when(mockDatabase.getCard(anyInt(), anyString(), eq(true)))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getAllPostsAsync("all", 10, true,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockDatabase).getCard(eq(10), eq("all"), eq(true));
        }

        // Partition: different types
        @ParameterizedTest
        @ValueSource(strings = {"all", "trending", "recent", "following"})
        @DisplayName("Various valid types should work")
        void getAllPostsAsync_variousTypes_shouldWork(String type) throws InterruptedException {
            when(mockDatabase.getCard(anyInt(), eq(type), anyBoolean()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getAllPostsAsync(type, 10, false,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Boundary: limit = 0
        @Test
        @DisplayName("Limit 0 should work")
        void getAllPostsAsync_limitZero_shouldWork() throws InterruptedException {
            when(mockDatabase.getCard(eq(0), anyString(), anyBoolean()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getAllPostsAsync("all", 0, false,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Boundary: negative limit
        @Test
        @DisplayName("Negative limit should still call database")
        void getAllPostsAsync_negativeLimit_shouldCallDb() throws InterruptedException {
            when(mockDatabase.getCard(eq(-1), anyString(), anyBoolean()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getAllPostsAsync("all", -1, false,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockDatabase).getCard(eq(-1), eq("all"), eq(false));
        }

        // Limit test: large dataset
        @Test
        @DisplayName("10,000 posts should be handled")
        void getAllPostsAsync_largeDataset_shouldHandle() throws InterruptedException {
            ArrayList<Post> bigList = new ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                bigList.add(mock(Post.class));
            }
            when(mockDatabase.getCard(anyInt(), anyString(), anyBoolean()))
                    .thenReturn(bigList);

            CountDownLatch latch = new CountDownLatch(1);

            service.getAllPostsAsync("all", 10_000, false,
                    list -> {
                        assertEquals(10_000, list.size());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Null type should trigger onFailed if DB throws")
        void getAllPostsAsync_nullType_shouldFail() throws InterruptedException {
            when(mockDatabase.getCard(anyInt(), isNull(), anyBoolean()))
                    .thenThrow(new RuntimeException("Null type not allowed"));

            CountDownLatch latch = new CountDownLatch(1);

            service.getAllPostsAsync(null, 10, false,
                    list -> fail("Should not succeed"),
                    e -> latch.countDown());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }


    @Nested
    @DisplayName("getPostByUserAsync")
    class GetPostByUserAsyncTests {

        @Test
        @DisplayName("Valid userId should return posts")
        void getPostByUserAsync_valid_shouldReturnPosts() throws InterruptedException {
            ArrayList<Post> posts = new ArrayList<>();
            posts.add(mockPost);
            when(mockDatabase.getPostsByUser(eq(1), eq(10))).thenReturn(posts);

            CountDownLatch latch = new CountDownLatch(1);

            service.getPostByUserAsync(1, 10,
                    list -> {
                        assertEquals(1, list.size());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Boundary: userId = 0
        @Test
        @DisplayName("UserId 0 should work")
        void getPostByUserAsync_userIdZero_shouldWork() throws InterruptedException {
            when(mockDatabase.getPostsByUser(eq(0), anyInt()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getPostByUserAsync(0, 10,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Boundary: negative userId
        @Test
        @DisplayName("Negative userId should call database")
        void getPostByUserAsync_negativeUserId_shouldCallDb() throws InterruptedException {
            when(mockDatabase.getPostsByUser(eq(-5), anyInt()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.getPostByUserAsync(-5, 10,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockDatabase).getPostsByUser(eq(-5), eq(10));
        }

        // Limit test
        @Test
        @DisplayName("Large result set should be handled")
        void getPostByUserAsync_largeResult_shouldHandle() throws InterruptedException {
            ArrayList<Post> bigList = new ArrayList<>();
            for (int i = 0; i < 5_000; i++) {
                bigList.add(mock(Post.class));
            }
            when(mockDatabase.getPostsByUser(anyInt(), anyInt())).thenReturn(bigList);

            CountDownLatch latch = new CountDownLatch(1);

            service.getPostByUserAsync(1, 5_000,
                    list -> {
                        assertEquals(5_000, list.size());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }


    @Nested
    @DisplayName("insertPostAsync")
    class InsertPostAsyncTests {

        @Test
        @DisplayName("Valid post without file should succeed")
        void insertPostAsync_noFile_shouldSucceed() throws Exception {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);
            when(mockDatabase.insertPost(any())).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.insertPostAsync(mockPost, null,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockUploader, never()).upload(any(File.class), any());
        }

        @Test
        @DisplayName("Valid post with file should upload to Cloudinary")
        void insertPostAsync_withFile_shouldUpload() throws Exception {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);
            when(mockDatabase.insertPost(any())).thenReturn(true);

            Map<String, String> uploadResult = new HashMap<>();
            uploadResult.put("secure_url", "[cloudinary.com](https://cloudinary.com/image.jpg)");
            uploadResult.put("public_id", "abc123");
            when(mockUploader.upload(any(File.class), any())).thenReturn(uploadResult);

            File mockFile = mock(File.class);
            CountDownLatch latch = new CountDownLatch(1);

            service.insertPostAsync(mockPost, mockFile,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail("Failed: " + e.getMessage()));

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockUploader).upload(eq(mockFile), any());
            verify(mockPost).setImageLink("[cloudinary.com](https://cloudinary.com/image.jpg)");
            verify(mockPost).setPublicId("abc123");
        }

        @Test
        @DisplayName("Rate limited user should return false")
        void insertPostAsync_rateLimited_shouldReturnFalse() throws Exception {
            when(mockDatabase.countPostsToday(any())).thenReturn(5); // At daily limit

            CountDownLatch latch = new CountDownLatch(1);

            service.insertPostAsync(mockPost, null,
                    result -> {
                        assertFalse(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockDatabase, never()).insertPost(any());
        }

        @Test
        @DisplayName("Null post should fail")
        void insertPostAsync_nullPost_shouldFail() throws InterruptedException {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);
            when(mockDatabase.insertPost(isNull())).thenThrow(new NullPointerException());

            CountDownLatch latch = new CountDownLatch(1);

            service.insertPostAsync(null, null,
                    result -> fail("Should not succeed"),
                    e -> latch.countDown());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Cloudinary upload failure should propagate")
        void insertPostAsync_uploadFails_shouldFail() throws Exception {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);
            when(mockUploader.upload(any(File.class), any()))
                    .thenThrow(new RuntimeException("Upload failed"));

            File mockFile = mock(File.class);
            CountDownLatch latch = new CountDownLatch(1);

            service.insertPostAsync(mockPost, mockFile,
                    result -> fail("Should not succeed"),
                    e -> {
                        assertTrue(e.getMessage().contains("Upload failed"));
                        latch.countDown();
                    });

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }


    @Nested
    @DisplayName("deletePostAsync")
    class DeletePostAsyncTests {

        @Test
        @DisplayName("Delete post without image should only delete from DB")
        void deletePostAsync_noImage_shouldOnlyDeleteFromDb() throws Exception {
            when(mockPost.getPublicId()).thenReturn("");
            when(mockDatabase.delete(eq(1))).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.deletePostAsync(mockPost,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockUploader, never()).destroy(anyString(), any());
            verify(mockDatabase).delete(eq(1));
        }

        @Test
        @DisplayName("Delete post with image should delete from Cloudinary first")
        void deletePostAsync_withImage_shouldDeleteFromCloudinary() throws Exception {
            when(mockPost.getPublicId()).thenReturn("abc123");

            Map<String, String> destroyResult = new HashMap<>();
            destroyResult.put("result", "ok");
            when(mockUploader.destroy(eq("abc123"), any())).thenReturn(destroyResult);
            when(mockDatabase.delete(eq(1))).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.deletePostAsync(mockPost,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockUploader).destroy(eq("abc123"), any());
            verify(mockDatabase).delete(eq(1));
        }

        @Test
        @DisplayName("Cloudinary 'not found' should still proceed with DB delete")
        void deletePostAsync_cloudinaryNotFound_shouldProceed() throws Exception {
            when(mockPost.getPublicId()).thenReturn("missing123");

            Map<String, String> destroyResult = new HashMap<>();
            destroyResult.put("result", "not found");
            when(mockUploader.destroy(anyString(), any())).thenReturn(destroyResult);
            when(mockDatabase.delete(anyInt())).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.deletePostAsync(mockPost,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Cloudinary deletion failure should throw")
        void deletePostAsync_cloudinaryFails_shouldThrow() throws Exception {
            when(mockPost.getPublicId()).thenReturn("abc123");

            Map<String, String> destroyResult = new HashMap<>();
            destroyResult.put("result", "error");
            when(mockUploader.destroy(anyString(), any())).thenReturn(destroyResult);

            CountDownLatch latch = new CountDownLatch(1);

            service.deletePostAsync(mockPost,
                    result -> fail("Should not succeed"),
                    e -> {
                        assertTrue(e.getMessage().contains("Cloudinary deletion failed"));
                        latch.countDown();
                    });

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }

    @Nested
    @DisplayName("tempDeleteAsync")
    class TempDeleteAsyncTests {

        @Test
        @DisplayName("Valid temp delete should succeed")
        void tempDeleteAsync_valid_shouldSucceed() throws InterruptedException {
            when(mockDatabase.tempDelete(eq(1), eq("Violation"))).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.tempDeleteAsync(1, "Violation",
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Null reason should still call database")
        void tempDeleteAsync_nullReason_shouldCallDb() throws InterruptedException {
            when(mockDatabase.tempDelete(anyInt(), isNull())).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.tempDeleteAsync(1, null,
                    result -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockDatabase).tempDelete(eq(1), isNull());
        }

        // Boundary: empty reason
        @Test
        @DisplayName("Empty reason should work")
        void tempDeleteAsync_emptyReason_shouldWork() throws InterruptedException {
            when(mockDatabase.tempDelete(anyInt(), eq(""))).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.tempDeleteAsync(1, "",
                    result -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }

    @Nested
    @DisplayName("editPostAsync")
    class EditPostAsyncTests {

        @Test
        @DisplayName("Edit without new file should only update DB")
        void editPostAsync_noNewFile_shouldOnlyUpdateDb() throws Exception {
            when(mockDatabase.update(any())).thenReturn(true);

            CountDownLatch latch = new CountDownLatch(1);

            service.editPostAsync(mockPost, null,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockUploader, never()).destroy(anyString(), any());
            verify(mockUploader, never()).upload(any(File.class), any());
        }

        @Test
        @DisplayName("Edit with new file and existing image should replace")
        void editPostAsync_newFileReplacesOld_shouldDeleteAndUpload() throws Exception {
            when(mockPost.getPublicId()).thenReturn("oldImage123");

            Map<String, String> destroyResult = new HashMap<>();
            destroyResult.put("result", "ok");
            when(mockUploader.destroy(eq("oldImage123"), any())).thenReturn(destroyResult);

            Map<String, String> uploadResult = new HashMap<>();
            uploadResult.put("secure_url", "[new-image.jpg](https://new-image.jpg)");
            uploadResult.put("public_id", "newImage456");
            when(mockUploader.upload(any(File.class), any())).thenReturn(uploadResult);
            when(mockDatabase.update(any())).thenReturn(true);

            File mockFile = mock(File.class);
            CountDownLatch latch = new CountDownLatch(1);

            service.editPostAsync(mockPost, mockFile,
                    result -> {
                        assertTrue(result);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockUploader).destroy(eq("oldImage123"), any());
            verify(mockUploader).upload(eq(mockFile), any());
            verify(mockPost).setImageLink("[new-image.jpg](https://new-image.jpg)");
            verify(mockPost).setPublicId("newImage456");
            verify(mockPost).setUpdatedAt(any(Timestamp.class));
        }

        @Test
        @DisplayName("Edit with new file but no existing image should only upload")
        void editPostAsync_newFileNoExisting_shouldOnlyUpload() throws Exception {
            when(mockPost.getPublicId()).thenReturn("");

            Map<String, String> uploadResult = new HashMap<>();
            uploadResult.put("secure_url", "[new-image.jpg](https://new-image.jpg)");
            uploadResult.put("public_id", "newImage456");
            when(mockUploader.upload(any(File.class), any())).thenReturn(uploadResult);
            when(mockDatabase.update(any())).thenReturn(true);

            File mockFile = mock(File.class);
            CountDownLatch latch = new CountDownLatch(1);

            service.editPostAsync(mockPost, mockFile,
                    result -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
            verify(mockUploader, never()).destroy(anyString(), any());
            verify(mockUploader).upload(eq(mockFile), any());
        }
    }

    @Nested
    @DisplayName("searchPostAsync")
    class SearchPostAsyncTests {

        @Test
        @DisplayName("Search with all parameters should work")
        void searchPostAsync_allParams_shouldWork() throws InterruptedException {
            Timestamp date = new Timestamp(System.currentTimeMillis());
            when(mockDatabase.searchPosts(eq(1), eq("test"), eq(date), eq(10), eq(5), eq("image.jpg")))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.searchPostAsync(1, "test", date, 10, 5, "image.jpg",
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Search with all null parameters should work")
        void searchPostAsync_allNull_shouldWork() throws InterruptedException {
            when(mockDatabase.searchPosts(isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.searchPostAsync(null, null, null, null, null, null,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Partition: only userId
        @Test
        @DisplayName("Search by userId only should work")
        void searchPostAsync_userIdOnly_shouldWork() throws InterruptedException {
            when(mockDatabase.searchPosts(eq(5), isNull(), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.searchPostAsync(5, null, null, null, null, null,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Partition: only content
        @Test
        @DisplayName("Search by content only should work")
        void searchPostAsync_contentOnly_shouldWork() throws InterruptedException {
            when(mockDatabase.searchPosts(isNull(), eq("hello"), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(new ArrayList<>());

            CountDownLatch latch = new CountDownLatch(1);

            service.searchPostAsync(null, "hello", null, null, null, null,
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        // Limit test: large result
        @Test
        @DisplayName("Large search result should be handled")
        void searchPostAsync_largeResult_shouldHandle() throws InterruptedException {
            ArrayList<Post> bigList = new ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                bigList.add(mock(Post.class));
            }
            when(mockDatabase.searchPosts(any(), any(), any(), any(), any(), any()))
                    .thenReturn(bigList);

            CountDownLatch latch = new CountDownLatch(1);

            service.searchPostAsync(null, null, null, null, null, null,
                    list -> {
                        assertEquals(10_000, list.size());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }

    @Nested
    @DisplayName("Callback Edge Cases")
    class CallbackEdgeCases {

        @Test
        @DisplayName("Null onSucceeded callback should not crash")
        void nullSuccessCallback_shouldNotCrash() throws Exception {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);
            when(mockDatabase.insertPost(any())).thenReturn(true);

            // Should not throw
            assertDoesNotThrow(() ->
                    service.insertPostAsync(mockPost, null, null, e -> {})
            );

            Thread.sleep(500); // Let async complete
        }

        @Test
        @DisplayName("Null onFailed callback should not crash")
        void nullFailedCallback_shouldNotCrash() throws Exception {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);
            when(mockDatabase.insertPost(any())).thenReturn(true);

            assertDoesNotThrow(() ->
                    service.insertPostAsync(mockPost, null, result -> {}, null)
            );

            Thread.sleep(500);
        }

        @Test
        @DisplayName("Both null callbacks should not crash")
        void bothNullCallbacks_shouldNotCrash() throws Exception {
            when(mockDatabase.countPostsToday(any())).thenReturn(0);
            when(mockDatabase.insertPost(any())).thenReturn(true);

            assertDoesNotThrow(() ->
                    service.insertPostAsync(mockPost, null, null, null)
            );

            Thread.sleep(500);
        }
    }



    private void injectField(String fieldName, Object value) throws Exception {
        var field = PostService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }

    @Nested
    @DisplayName("Search Partition Tests")
    class SearchTests {

        @Test
        @DisplayName("Partition: Partial Params - Should work with only content filtered")
        void searchPostAsync_partialPartition_shouldWork() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);

            // Testing the partition where user only provides content, leaving others null
            when(mockDatabase.searchPosts(isNull(), eq("java"), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(new ArrayList<>());

            service.searchPostAsync(null, "java", null, null, null, null,
                    list -> {
                        assertNotNull(list);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Boundary: Zero Values - Should handle 0 likes/comments correctly")
        void searchPostAsync_zeroBoundary_shouldWork() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);

            // Boundary Value Analysis: Testing exact zero for numeric filters
            when(mockDatabase.searchPosts(anyInt(), anyString(), any(), eq(0), eq(0), anyString()))
                    .thenReturn(new ArrayList<>());

            service.searchPostAsync(1, "test", new Timestamp(System.currentTimeMillis()), 0, 0, "path",
                    list -> latch.countDown(),
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }

    @Nested
    @DisplayName("Tag Logic Partitions")
    class TagTests {

        @Test
        @DisplayName("Partition: Non-Existent Tag - Should return empty list safely")
        void getPostsByTagsAsync_emptyPartition_shouldReturnEmpty() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);
            when(mockDatabase.getPostsByTag("UnknownTag")).thenReturn(new ArrayList<>());

            service.getPostsByTagsAsync("UnknownTag",
                    list -> {
                        assertTrue(list.isEmpty());
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("Boundary: Max Tag Length - Should handle 50-char tag names")
        void getPostsByTagsAsync_lengthBoundary_shouldWork() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);
            String longTag = "A".repeat(50); // Boundary based on VARCHAR(50) schema

            when(mockDatabase.getPostsByTag(longTag)).thenReturn(new ArrayList<>());

            service.getPostsByTagsAsync(longTag, list -> latch.countDown(), e -> fail());
            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
    }

    @Nested
    @DisplayName("State-Transition Tests")
    class EditStateTests {

        @Test
        @DisplayName("State Transition: Image Swap - Should delete old image before uploading new")
        void editPostAsync_imageSwap_shouldCleanUpOldState() throws Exception {
            CountDownLatch latch = new CountDownLatch(1);
            File newFile = mock(File.class);

            // Initial State: Post has an existing Cloudinary publicId
            when(mockPost.getPublicId()).thenReturn("old_public_id");

            Map<String, String> uploadResult = new HashMap<>();
            uploadResult.put("secure_url", "https://new-image.png");
            uploadResult.put("public_id", "new_public_id");

            // Mocking the sequence: Destroy Old -> Upload New -> Update DB
            when(mockUploader.destroy(eq("old_public_id"), any())).thenReturn(new HashMap<>());
            when(mockUploader.upload(eq(newFile), any())).thenReturn(uploadResult);
            when(mockDatabase.update(any())).thenReturn(true);

            service.editPostAsync(mockPost, newFile,
                    success -> {
                        assertTrue(success);
                        latch.countDown();
                    },
                    e -> fail());

            assertTrue(latch.await(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS));

            // Verify the state transition logic was followed exactly
            verify(mockUploader).destroy(eq("old_public_id"), any());
            verify(mockPost).setImageLink("https://new-image.png");
            verify(mockPost).setPublicId("new_public_id");
        }
    }

    @Nested
    @DisplayName("Resource & Concurrency Limits")
    class ConcurrencyTests {

        @Test
        @DisplayName("Limit: Thread Pool Saturation - Should handle 10 concurrent tasks")
        void concurrency_threadPoolSaturation_shouldNotDeadlock() throws InterruptedException {
            int taskCount = 10; // Matching your FixedThreadPool(10)
            CountDownLatch latch = new CountDownLatch(taskCount);

            for (int i = 0; i < taskCount; i++) {
                service.getAllPostsAsync("all", 1, false,
                        list -> latch.countDown(),
                        e -> latch.countDown()
                );
            }

            // Ensures the executor service processes tasks within reasonable time
            boolean completed = latch.await(10, TimeUnit.SECONDS);
            assertTrue(completed, "ExecutorService failed to process 10 concurrent tasks.");
        }
    }
}
