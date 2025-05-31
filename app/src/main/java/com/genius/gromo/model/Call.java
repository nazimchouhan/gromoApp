package com.genius.gromo.model;

public class Call {

    // Name of the calling agent
    private String name;
    private String recordingId;
    private String month;
    private String date;
    private String year;

    public Call(String name, String recordingId,String month,String date,String year){
        this.name = name;
        this.recordingId = recordingId;
        this.date = date;
        this.month=month;
        this.year=year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

}

