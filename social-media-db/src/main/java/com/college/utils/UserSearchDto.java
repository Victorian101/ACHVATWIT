package com.college.utils;

public class UserSearchDto {
    private String username;
    private String url;
    private boolean isFollow;

    public UserSearchDto(String username, String url, boolean isFollow) {
        this.username = username;
        this.url = url;
        this.isFollow = isFollow;
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

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }
}
