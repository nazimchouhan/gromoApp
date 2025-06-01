package com.genius.gromo.model;

public class Call {

    // Name of the calling agent
    private String name;
    private String recordingId;

    public Call(String name, String recordingId){
        this.name = name;
        this.recordingId = recordingId;

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
}

