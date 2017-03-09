package com.novauc;

/**
 * Created by james on 3/9/17.
 */
public class User {
    int id;
    String username;

    User(int id, String name) {
        this.id = id;
        this.username = name;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
