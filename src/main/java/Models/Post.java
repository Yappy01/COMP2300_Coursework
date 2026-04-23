package Models;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Post extends AdminEntity {
    private int postId;
    private int userId;
    private String content;
    private int likeCount;
    private int commentCount;
    private String imageLink;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String publicId;
    private String reasonDeleted;
    private ArrayList<Tag> tagsList;

    // Constructor (full)
    public Post(int postId, int userId, String content, int likeCount,
                String imageLink, Timestamp createdAt, Timestamp updatedAt, int commentCount, String publicId, String reasonDeleted) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.imageLink = imageLink;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publicId = publicId;
        this.reasonDeleted = reasonDeleted;
        this.tagsList = new ArrayList<>();
    }

    // Constructor (for new post)
    public Post(int userId, String content, String imageLink, String publicId ,ArrayList<Tag> tagsList) {
        this.userId = userId;
        this.content = content;
        this.imageLink = imageLink;
        this.publicId = publicId;
        this.likeCount = 0;
        this.commentCount = 0;
        this.tagsList = tagsList;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getReasonDeleted() {
        return reasonDeleted;
    }

    public void setReasonDeleted(String reasonDeleted) {
        this.reasonDeleted = reasonDeleted;
    }

    public ArrayList<Tag> getTagsList() {
        return tagsList;
    }

    public void setTagsList(ArrayList<Tag> tagsList) {
        this.tagsList = tagsList;
    }
}