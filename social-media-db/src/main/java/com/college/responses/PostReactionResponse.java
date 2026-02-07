package com.college.responses;

public class PostReactionResponse extends BasicResponse{
    private int postId;
    private int likesCount;
    private int dislikesCount;
    private int myReaction;

    public PostReactionResponse(boolean success, Integer errorCode, int postId, int likesCount, int dislikesCount, int myReaction) {
        super(success, errorCode);
        this.postId = postId;
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
        this.myReaction = myReaction;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public int getMyReaction() {
        return myReaction;
    }

    public void setMyReaction(int myReaction) {
        this.myReaction = myReaction;
    }
}
