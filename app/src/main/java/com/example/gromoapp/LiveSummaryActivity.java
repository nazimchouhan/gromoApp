package com.example.gromoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.genius.gromo.service.LiveCallWebSocketClient;

public class LiveSummaryActivity extends AppCompatActivity implements LiveCallWebSocketClient.MessageCallback {
    private static final String TAG = "LiveSummaryActivity";
    
    // Views
    private TextView transcriptionText;
    private TextView sentimentScore;
    private TextView emojiText;
    private ProgressBar sentimentProgress;
    private RecyclerView recyclerView;
   // private ReminderAdapter adapter;
    private TextView summaryText;
    private TextView topicsText;
    private TextView callDurationText;
    private EditText objectionsInput;
    private EditText feedbackInput;
    private EditText improvementInput;
    TextView feedback1,feedback2,feedback3;

    // State
    private boolean isMicMuted = false;
    private LiveCallWebSocketClient webSocketClient;
    private Handler mainHandler;
    private String phoneNumber;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_summary);

        mainHandler = new Handler(Looper.getMainLooper());
        
        // Get user details from SharedPreferences
//        SharedPreferences prefs = getSharedPreferences("UserPrefsSummary", MODE_PRIVATE);
//        phoneNumber = prefs.getString("userPhone", null);
//        userName = prefs.getString("userName", null);
//
//        if (phoneNumber == null || userName == null) {
//            Toast.makeText(this, "No call details found", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }

        try {
            initializeViews();
            setupWebSocket();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing activity: " + e.getMessage());
            Toast.makeText(this, "Error initializing live summary", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        transcriptionText = findViewById(R.id.transcriptionText);
        if (transcriptionText == null) {
            throw new IllegalStateException("Required view 'transcriptionText' not found");
        }
        feedback1=findViewById(R.id.reminder_text1);
        feedback2=findViewById(R.id.reminder_text2);
        feedback3=findViewById(R.id.reminder_text3);
        sentimentScore = findViewById(R.id.sentimentScore);
        emojiText = findViewById(R.id.emojiText);
        sentimentProgress = findViewById(R.id.sentimentProgress);
        summaryText = findViewById(R.id.summaryText);
        topicsText = findViewById(R.id.topicsText);
        callDurationText = findViewById(R.id.callDurationText);
        objectionsInput = findViewById(R.id.objectionsInput);
        feedbackInput = findViewById(R.id.feedbackInput);
        improvementInput = findViewById(R.id.improvementInput);

        //recyclerView = findViewById(R.id.remindersRecyclerView);
//        if (recyclerView != null) {
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            adapter = new ReminderAdapter(new ArrayList<>());
//            recyclerView.setAdapter(adapter);
//        } else {
//            throw new IllegalStateException("Required view 'remindersRecyclerView' not found");
//        }

        setupButtons();
    }

    private void setupButtons() {
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton closeButton = findViewById(R.id.closeButton);
        ImageButton micButton = findViewById(R.id.micButton);
        ImageButton volumeButton = findViewById(R.id.volumeButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> finish());
        }
        if (micButton != null) {
            micButton.setOnClickListener(this::onMicButtonClick);
        }
        if (volumeButton != null) {
            volumeButton.setOnClickListener(this::onVolumeButtonClick);
        }
    }

    private void setupWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.stopWebSocket();
        }
        webSocketClient = new LiveCallWebSocketClient(this, this);
        webSocketClient.startWebSocket();
    }

    @Override
    public void onMessageReceived(JSONObject message) {
        if (mainHandler == null) return;

        runOnUiThread(() -> {
            try {
                if(message == null || message.get("event").equals("start") || message.get("event").equals("ping")){
                    Log.e("", (String)message.get("event"));
                }
                else processReceivedData(message);
            } catch (JSONException e) {
                Log.e(TAG, "Error processing message: " + e.getMessage());
                Toast.makeText(this, "Error processing data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processReceivedData(JSONObject jsonObject) throws JSONException {
//        if (jsonObject == null || jsonObject.isEmpty()) {
        if (jsonObject == null || jsonObject.get("event").equals("ping") || jsonObject.get("event").equals("start")) {
            Log.w(TAG, "Received empty message");
            return;
        }

//        JSONObject json = new JSONObject(jsonStr);
        
        // Update transcription
        String content = jsonObject.optString("message", "");
        updateTranscription(content);

        // Update action items
        JSONArray nextSteps = jsonObject.optJSONArray("next_action_steps");
        List<String> actionItems = new ArrayList<>();
        if (nextSteps != null) {
            for (int i = 0; i < nextSteps.length(); i++) {
                actionItems.add(nextSteps.optString(i, ""));
            }
        }
        updateActionItems(actionItems);

        // Update sentiment
        int score = jsonObject.optInt("sentiment_score", 0);
        String emoji = jsonObject.optString("neutrality_emoji", "😐");
        String objectionRadar = jsonObject.optString("objection_radar", "No objections detected");
        updateSentiment(score, emoji, objectionRadar);

        Log.e("Updated "+ score, content);
    }

    private void updateTranscription(String text) {
        if (transcriptionText != null) {
            transcriptionText.setText(text);
        }
    }

    private void updateSentiment(int score, String emoji, String objectionRadar) {
        if (sentimentScore != null) {
            sentimentScore.setText(String.valueOf(score));
        }
        if (emojiText != null) {
            emojiText.setText(emoji);
        }
        if (sentimentProgress != null) {
            sentimentProgress.setProgress(score);
        }
        if (summaryText != null) {
            summaryText.setText(objectionRadar);
        }
    }

    private void updateActionItems(List<String> items) {

        if(items.size() >= 1 && items.get(0)!=null){
            feedback1.setText(items.get(0));
        }
        if(items.size() >= 2 && items.get(1)!=null){
            feedback2.setText(items.get(1));
        }
        if(items.size() >= 3 && items.get(2)!=null){
            feedback3.setText(items.get(2));
        }
    }

    private void onMicButtonClick(View view) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            isMicMuted = !isMicMuted;
            audioManager.setMicrophoneMute(isMicMuted);
            String status = isMicMuted ? "Muted" : "Unmuted";
            Toast.makeText(this, "Microphone " + status, Toast.LENGTH_SHORT).show();
        }
    }

    private void onVolumeButtonClick(View view) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(true);
            Toast.makeText(this, "Speakerphone ON", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webSocketClient != null) {
            webSocketClient.stopWebSocket();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupWebSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.stopWebSocket();
            webSocketClient = null;
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler = null;
        }
    }
} 