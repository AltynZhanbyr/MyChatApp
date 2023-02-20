package com.example.mychatapp.model;

import java.io.Serializable;

public class ModelUser implements Serializable {
    private String email;
    private String id;
    private String name;
    private String userAvatar;

    public ModelUser(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    @Override
    public String toString() {
        return "ModelUser{" +
                "email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                '}';
    }

}
