package com.college.responses;

public class PostResponce extends BasicResponse{
    private String content;

    public PostResponce(boolean success, Integer errorCode, String content){
        super(success, errorCode);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
