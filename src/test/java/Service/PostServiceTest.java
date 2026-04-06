package Service;

import DBHandling.ComPostDatabase;
import Models.Comment;
import Models.Post;
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

class PostServiceTest {

    private PostService service;

    @Mock
    private ComPostDatabase mockDatabase;

    @Mock
    private Post mockPost;
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

        mockUser = new User(1, "tester","test@gmail.com");
        Session.startSession(mockUser);

        service = new PostService();

        // Inject mock database via reflection (since it's private final)
        var field = PostService.class.getDeclaredField("postDatabase");
        field.setAccessible(true);
        field.set(service, mockDatabase);

        when(mockPost.getPostId()).thenReturn(1);
        when(mockPost.getUserId()).thenReturn(1);
    }

    // =====================================================
    // 🟩 likePost — Boundary + Partition
    // =====================================================

    @Test
    void likePost_valid_shouldCallDatabase() {
        service.likePost(mockPost);

        verify(mockDatabase).toggleLike(eq(1), anyInt());
    }

    @Test
    void likePost_nullPost_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            service.likePost(null);
        });
    }

    @Test
    void likePost_invalidPostId_shouldStillCallDB() {
        when(mockPost.getPostId()).thenReturn(-1);

        service.likePost(mockPost);

        verify(mockDatabase).toggleLike(eq(-1), anyInt());
    }

    // =====================================================
    // 🟩 commentPost — Boundary + Partition + Limit
    // =====================================================

    @Test
    void commentPost_valid_shouldInsert() {
        service.commentPost(mockPost, "Nice post");

        verify(mockDatabase).addComment(eq(1), eq(1), eq("Nice post"));
    }

    @Test
    void commentPost_nullContent_shouldNotInsert() {
        service.commentPost(mockPost, null);

        verify(mockDatabase, never()).addComment(anyInt(), anyInt(), anyString());
    }

    @Test
    void commentPost_emptyContent_shouldNotInsert() {
        service.commentPost(mockPost, "");

        verify(mockDatabase, never()).addComment(anyInt(), anyInt(), anyString());
    }

    @Test
    void commentPost_whitespaceContent_shouldInsert_bug() {
        service.commentPost(mockPost, "   ");

        // This reveals a bug — whitespace passes validation
        verify(mockDatabase).addComment(anyInt(), anyInt(), eq("   "));
    }

    @Test
    void commentPost_largeContent_limitTest() {
        String large = "A".repeat(10000);

        service.commentPost(mockPost, large);

        verify(mockDatabase).addComment(eq(1), eq(1), eq(large));
    }

    @Test
    void commentPost_nullPost_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> {
            service.commentPost(null, "Hello");
        });
    }

    // =====================================================
    // 🟩 getCommentsAsync — Async + Boundary + Limit
    // =====================================================

    @Test
    void getCommentsAsync_valid_shouldReturnList() throws InterruptedException {
        when(mockDatabase.getComment(anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());

        CountDownLatch latch = new CountDownLatch(1);

        service.getCommentsAsync(mockPost,
                list -> {
                    assertNotNull(list);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void getCommentsAsync_largeList_limitTest() throws InterruptedException {
        ArrayList<Comment> bigList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            bigList.add(mock(Comment.class));
        }

        when(mockDatabase.getComment(anyInt(), anyInt()))
                .thenReturn(bigList);

        CountDownLatch latch = new CountDownLatch(1);

        service.getCommentsAsync(mockPost,
                list -> {
                    assertEquals(10000, list.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 getAllPostsAsync — Partition + Boundary + Limit
    // =====================================================

    @Test
    void getAllPostsAsync_validType_shouldReturnList() throws InterruptedException {
        when(mockDatabase.getCard(anyInt(), eq("all")))
                .thenReturn(new ArrayList<>());

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllPostsAsync("all",
                list -> {
                    assertNotNull(list);
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void getAllPostsAsync_invalidType_shouldFail() throws InterruptedException {
        when(mockDatabase.getCard(anyInt(), eq("invalid")))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllPostsAsync("invalid",
                list -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void getAllPostsAsync_nullType_shouldFail() throws InterruptedException {
        when(mockDatabase.getCard(anyInt(), isNull()))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllPostsAsync(null,
                list -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void getAllPostsAsync_largeDataset_limitTest() throws InterruptedException {
        ArrayList<Post> bigList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            bigList.add(mock(Post.class));
        }

        when(mockDatabase.getCard(anyInt(), any()))
                .thenReturn(bigList);

        CountDownLatch latch = new CountDownLatch(1);

        service.getAllPostsAsync("all",
                list -> {
                    assertEquals(10000, list.size());
                    latch.countDown();
                },
                e -> fail());

        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 insertPostAsync — Boundary + Partition
    // =====================================================

    @Test
    void insertPostAsync_valid_shouldSucceed() throws InterruptedException {
        when(mockDatabase.insertPost(any())).thenReturn(true);

        CountDownLatch latch = new CountDownLatch(1);

        service.insertPostAsync(mockPost,
                latch::countDown,
                e -> fail());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void insertPostAsync_nullPost_shouldFail() throws InterruptedException {
        when(mockDatabase.insertPost(isNull()))
                .thenThrow(new RuntimeException());

        CountDownLatch latch = new CountDownLatch(1);

        service.insertPostAsync(null,
                () -> fail(),
                e -> latch.countDown());

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    // =====================================================
    // 🟩 Async Edge Cases
    // =====================================================

    @Test
    void nullCallbacks_shouldNotCrash() {
        when(mockDatabase.insertPost(any())).thenReturn(true);

        assertDoesNotThrow(() ->
                service.insertPostAsync(mockPost, null, null)
        );
    }
}