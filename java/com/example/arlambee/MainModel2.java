package com.example.arlambee;

public class MainModel2 {
    String time, description;
    String id;

    public MainModel2(String time, String description,String id) {
        this.time = time;
        this.description = description;
        this.id=id;
    }

    public MainModel2() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
