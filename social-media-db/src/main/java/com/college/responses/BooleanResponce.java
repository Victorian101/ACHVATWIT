package com.college.responses;

public class BooleanResponce extends BasicResponse{
    private boolean bool;

    public BooleanResponce(boolean success, Integer errorCode, boolean bool){
        super(success, errorCode);
        this.bool = bool;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }
}
