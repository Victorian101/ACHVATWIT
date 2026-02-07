package com.college.responses;

import com.college.utils.User;

import java.util.ArrayList;
import java.util.List;

public class UsersResponse extends BasicResponse{
    private List<User> users = new ArrayList<>();

    public UsersResponse(boolean success, Integer errorCode, List<User> users){
        super(success, errorCode);
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
