package com.college.responses;

import com.college.utils.PostDto;

import java.util.List;

public class FeedResponce extends BasicResponse{
    private List<PostDto> posts;

    public FeedResponce(boolean success, Integer errorCode, List<PostDto> posts){
        super(success, errorCode);
        this.posts = posts;
    }

    public List<PostDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDto> posts) {
        this.posts = posts;
    }
}
