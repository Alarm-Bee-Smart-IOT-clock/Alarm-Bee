package com.example.arlambee;

public class UserManager {
    private static UserManager instance;
    private String username;

    public UserManager(String username) {
        this.username = username;
    }
    private UserManager() {

    }
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public static void setInstance(UserManager instance) {
        UserManager.instance = instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
