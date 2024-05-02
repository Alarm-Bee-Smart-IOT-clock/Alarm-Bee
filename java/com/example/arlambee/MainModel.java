package com.example.arlambee;

public class MainModel {
    String date;
    String reminderText;

    public MainModel(String date, String reminderText) {
        this.date = date;
        this.reminderText = reminderText;
    }

    public MainModel() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReminderText() {
        return reminderText;
    }

    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }
}
