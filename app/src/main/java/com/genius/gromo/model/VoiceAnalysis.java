package com.genius.gromo.model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VoiceAnalysis {
    private String transcription;
    private boolean status;
    private String recordingId;
    private String recording_url;
    private Sentiment sentiment;
    private String summary;
    private List<String> topics;
    private List<Entity> entities;
    private List<String> keyPhrases;
    private List<String> actionItems;
    private List<String> questions;

    public static VoiceAnalysis fromJson(JSONObject json) throws JSONException {
        VoiceAnalysis analysis = new VoiceAnalysis();
        analysis.transcription = json.getString("transcription");
        analysis.status = json.getBoolean("status");
        analysis.recordingId = json.getString("recording_id");
        analysis.recording_url = json.getString("recording_url");

        JSONObject analysisObj = json.getJSONObject("analysis_data");

        // Parse sentiment (handle null)
        if (!analysisObj.isNull("sentiment")) {
            JSONObject sentimentObj = analysisObj.getJSONObject("sentiment");
            analysis.sentiment = new Sentiment(
                    sentimentObj.getString("label"),
                    sentimentObj.getDouble("confidence"),
                    sentimentObj.getString("explanation")
            );
        }

        // Parse summary (handle null)
        analysis.summary = analysisObj.isNull("summary") ? "" : analysisObj.getString("summary");

        // Parse arrays (handle null)
        analysis.topics = analysisObj.isNull("topics") ? new ArrayList<>() : jsonArrayToList(analysisObj.getJSONArray("topics"));
        analysis.keyPhrases = analysisObj.isNull("key_phrases") ? new ArrayList<>() : jsonArrayToList(analysisObj.getJSONArray("key_phrases"));
        analysis.actionItems = analysisObj.isNull("action_items") ? new ArrayList<>() : jsonArrayToList(analysisObj.getJSONArray("action_items"));
        analysis.questions = analysisObj.isNull("questions") ? new ArrayList<>() : jsonArrayToList(analysisObj.getJSONArray("questions"));

        // Parse entities (handle null)
        analysis.entities = new ArrayList<>();
        if (!analysisObj.isNull("entities")) {
            JSONArray entitiesArray = analysisObj.getJSONArray("entities");
            for (int i = 0; i < entitiesArray.length(); i++) {
                JSONObject entityObj = entitiesArray.getJSONObject(i);
                analysis.entities.add(new Entity(
                        entityObj.getString("name"),
                        entityObj.getString("type")
                ));
            }
        }

        return analysis;
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }

    // Getters
    public String getTranscription() { return transcription; }
    public boolean isStatus() { return status; }
    public String getRecordingId() { return recordingId; }
    public String getRecording_url() { return recording_url; }
    public Sentiment getSentiment() { return sentiment; }
    public String getSummary() { return summary; }
    public List<String> getTopics() { return topics; }
    public List<Entity> getEntities() { return entities; }
    public List<String> getKeyPhrases() { return keyPhrases; }
    public List<String> getActionItems() { return actionItems; }
    public List<String> getQuestions() { return questions; }

    // Inner classes
    public static class Sentiment {
        private final String label;
        private final double confidence;
        private final String explanation;

        public Sentiment(String label, double confidence, String explanation) {
            this.label = label;
            this.confidence = confidence;
            this.explanation = explanation;
        }

        public String getLabel() { return label; }
        public double getConfidence() { return confidence; }
        public String getExplanation() { return explanation; }
    }

    public static class Entity {
        private final String name;
        private final String type;

        public Entity(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public String getType() { return type; }
    }
}

