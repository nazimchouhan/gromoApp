package com.genius.gromo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.gromoapp.R;
import com.genius.gromo.model.VoiceAnalysis;

import com.genius.gromo.service.VoiceAnalysisService;
import org.json.JSONObject;

public class CallSummary extends AppCompatActivity {

    private ProgressBar progressBar; // Your progress bar
//    private View contentLayout;
    private Toolbar toolbar;
    private TextView callDurationText;
    private EditText objectionsInput, feedbackInput, improvementInput;
    private TextView transcriptionText, sentimentText, summaryText, topicsText;
    private Button submitButton;
    private VoiceAnalysisService voiceAnalysisService;
    private long callDuration = 0;
    private static String recordingId1;
    private static String recordingId2;
    private static String recordingIdToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_summary);
        
        // Initialize views
        initializeViews();

        // Get recording IDs from different sources
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        recordingId1 = sharedPreferences.getString("recordingId", null);
        recordingId2 = getIntent().getStringExtra("recording_id");

        // Choose which recording ID to use
        recordingIdToUse = recordingId2 != null ? recordingId2 : recordingId1;
        Log.e("recordingid",recordingIdToUse);
        // Show progress and start analysis if we have a recording ID
        if (recordingIdToUse != null && !recordingIdToUse.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
//            contentLayout.setVisibility(View.GONE);
            analyzeRecording();
        } else {
            progressBar.setVisibility(View.GONE);
//            contentLayout.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No recording ID available", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity since we can't proceed without a recording ID
        }
    }

    private void initializeViews() {
//        contentLayout = findViewById(R.id.ContentLayout);
//        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        callDurationText = findViewById(R.id.callDurationText);
        objectionsInput = findViewById(R.id.objectionsInput);
        feedbackInput = findViewById(R.id.feedbackInput);
        improvementInput = findViewById(R.id.improvementInput);
        submitButton = findViewById(R.id.submitButton);
        transcriptionText = findViewById(R.id.transcriptionText);
        sentimentText = findViewById(R.id.sentimentText);
        summaryText = findViewById(R.id.summaryText);
        topicsText = findViewById(R.id.topicsText);

        // Initialize service
        voiceAnalysisService = new VoiceAnalysisService(this);

        // Setup UI and listeners
        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        long minutes = callDuration / 60;
        long seconds = callDuration % 60;
        callDurationText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        submitButton.setOnClickListener(v -> submitCallSummary());
    }

    private void analyzeRecording() {
        voiceAnalysisService.analyzeRecording(recordingIdToUse, new VoiceAnalysisService.ApiCallback(){
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Log.e("response",result.toString());
                    VoiceAnalysis analysis = VoiceAnalysis.fromJson(result);
                    runOnUiThread(() -> {
                        // Update your analysis data in the UI
                        updateUIWithAnalysis(analysis);

                        // Once data is set, hide progress bar AFTER layout is drawn
//                        contentLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//                            // Layout is fully visible at this point
//                            progressBar.setVisibility(View.GONE);
//                        });
                    });

                }catch (Exception e) {
                    Log.e(TAG, "Failed to parse analysis: " + e.getMessage());
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CallSummary.this,
                                "Failed to parse analysis: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CallSummary.this,
                            "Analysis failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateUIWithAnalysis(VoiceAnalysis analysis) {
        // Transcription
        if (transcriptionText != null) {
            String transcription = analysis.getTranscription();
            transcriptionText.setText(!transcription.isEmpty() ? transcription : "No transcription available");
        }

        // Sentiment
        if (sentimentText != null) {
            VoiceAnalysis.Sentiment sentiment = analysis.getSentiment();
            if (sentiment != null) {
                String sentimentInfo = String.format("Sentiment: %s (%.2f%%)\n%s",
                        sentiment.getLabel(),
                        sentiment.getConfidence() * 100,
                        sentiment.getExplanation());
                sentimentText.setText(sentimentInfo);
            } else {
                sentimentText.setText("Sentiment analysis not available");
            }
        }

        // Summary
        if (summaryText != null) {
            String summary = analysis.getSummary();
            summaryText.setText(!summary.isEmpty() ? summary : "No summary available");
        }

        // Topics
        if (topicsText != null && analysis.getTopics() != null && !analysis.getTopics().isEmpty()) {
            StringBuilder topicsBuilder = new StringBuilder("Topics discussed:\n");
            for (String topic : analysis.getTopics()) {
                topicsBuilder.append("• ").append(topic).append("\n");
            }
            topicsText.setText(topicsBuilder.toString());
        } else {
            topicsText.setText("No topics identified");
        }

        // Action Items
        if (analysis.getActionItems() != null && !analysis.getActionItems().isEmpty()) {
            improvementInput.setText(String.join("\n• ", analysis.getActionItems()));
        } else {
            improvementInput.setText("");
        }

        // Key Phrases
        if (analysis.getKeyPhrases() != null && !analysis.getKeyPhrases().isEmpty()) {
            feedbackInput.setText(String.join("\n• ", analysis.getKeyPhrases()));
        } else {
            feedbackInput.setText("");
        }

        // Questions
        if (analysis.getQuestions() != null && !analysis.getQuestions().isEmpty()) {
            objectionsInput.setText(String.join("\n• ", analysis.getQuestions()));
        } else {
            objectionsInput.setText("");
        }
    }
    private void submitCallSummary() {
        String objections = objectionsInput.getText().toString().trim();
        String feedback = feedbackInput.getText().toString().trim();
        String improvement = improvementInput.getText().toString().trim();

        if (objections.isEmpty() || feedback.isEmpty() || improvement.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Send data to backend

        Toast.makeText(this, "Call summary submitted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
