package com.college.utils;

public class PostReaction {
    private int postId;
    private String username;
    private int reaction;

    public PostReaction(int postId, String username, int reaction) {
        this.reaction = reaction;
        this.username = username;
        this.postId = postId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}
