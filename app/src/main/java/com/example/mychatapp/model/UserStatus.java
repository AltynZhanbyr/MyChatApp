package com.example.mychatapp.model;

public class UserStatus extends ModelUser {
    private boolean isOnline;

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

}
