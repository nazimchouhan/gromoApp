package com.example.gromoapp;

public class Reminder {
    private String title;
    private String type;

    public Reminder(String title, String type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
} 