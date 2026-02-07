package com.college.responses;

import com.college.utils.UserSearchDto;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class SearchUsersResponse extends BasicResponse{
    private List<UserSearchDto> users = new ArrayList<>();

    public SearchUsersResponse(boolean success, Integer errorCode, List<UserSearchDto> users) {
        super(success, errorCode);
        this.users = users;
    }

    public List<UserSearchDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserSearchDto> users) {
        this.users = users;
    }
}
