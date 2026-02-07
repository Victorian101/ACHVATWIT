package com.college.responses;

public class MeResponse extends BasicResponse{
    private String username;
    private String url;

    public MeResponse(boolean success, Integer errorCode, String username, String url) {
        super(success, errorCode);
        this.username = username;
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
