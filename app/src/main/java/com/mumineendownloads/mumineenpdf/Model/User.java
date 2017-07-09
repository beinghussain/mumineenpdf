package com.mumineendownloads.mumineenpdf.Model;

/**
 * Created by Hussain on 7/9/2017.
 */

public class User {
    String email;
    String name;
    int userId;

    public User() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
