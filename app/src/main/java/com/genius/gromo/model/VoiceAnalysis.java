package com.genius.gromo.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class VoiceAnalysis {
    private String transcription;
    private String language;
    private String languageName;
    private Sentiment sentiment;
    private String summary;
    private List<String> topics;
    private List<Entity> entities;
    private List<String> keyPhrases;
    private List<String> actionItems;
    private List<String> questions;

    public static VoiceAnalysis fromJson(JSONObject json) throws JSONException{
        VoiceAnalysis analysis = new VoiceAnalysis();
        analysis.transcription = json.getString("transcription");
        analysis.language = json.getString("language");
        analysis.languageName = json.getString("language_name");

        JSONObject analysisObj = json.getJSONObject("analysis");
        
        // Parse sentiment
        JSONObject sentimentObj = analysisObj.getJSONObject("sentiment");
        analysis.sentiment = new Sentiment(
            sentimentObj.getString("label"),
            sentimentObj.getDouble("confidence"),
            sentimentObj.getString("explanation")
        );

        analysis.summary = analysisObj.getString("summary");
        
        // Parse arrays
        analysis.topics = jsonArrayToList(analysisObj.getJSONArray("topics"));
        analysis.keyPhrases = jsonArrayToList(analysisObj.getJSONArray("key_phrases"));
        analysis.actionItems = jsonArrayToList(analysisObj.getJSONArray("action_items"));
        analysis.questions = jsonArrayToList(analysisObj.getJSONArray("questions"));

        // Parse entities
        analysis.entities = new ArrayList<>();
        JSONArray entitiesArray = analysisObj.getJSONArray("entities");
        for (int i = 0; i < entitiesArray.length(); i++) {
            JSONObject entityObj = entitiesArray.getJSONObject(i);
            analysis.entities.add(new Entity(
                entityObj.getString("name"),
                entityObj.getString("type")
            ));
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
    public String getLanguage() { return language; }
    public String getLanguageName() { return languageName; }
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









