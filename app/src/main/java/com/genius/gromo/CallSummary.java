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
    private View contentLayout;
    private Toolbar toolbar;
    private TextView callDurationText;
    private EditText objectionsInput, feedbackInput, improvementInput;
    private TextView transcriptionText, sentimentText, summaryText, topicsText;
    private Button submitButton;
    private VoiceAnalysisService voiceAnalysisService;
    private long callDuration = 0;
    private String recordingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_summary);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        String recordingId = sharedPreferences.getString("response_Id", "Default Name");

        contentLayout=findViewById(R.id.ContentLayout);
        progressBar = findViewById(R.id.progressBar); // e.g., a ProgressBar with id progressBar

        // Initially, show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get data from intent
        callDuration = getIntent().getLongExtra("CallDuration", 0L);

        // Initialize views
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

        // If we have a recording ID, analyze it
        if (recordingId != null && !recordingId.isEmpty()) {
            analyzeRecording();
        }
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
        voiceAnalysisService.analyzeRecording(recordingId, new VoiceAnalysisService.ApiCallback(){
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    VoiceAnalysis analysis = VoiceAnalysis.fromJson(result);
                    runOnUiThread(() -> {
                        // Update your analysis data in the UI
                        updateUIWithAnalysis(analysis);

                        // Once data is set, hide progress bar AFTER layout is drawn
                        contentLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                            // Layout is fully visible at this point
                            progressBar.setVisibility(View.GONE);
                        });
                    });

                } catch (Exception e) {
                    onFailure("Failed to parse analysis: " + e.getMessage());
                    Log.e(TAG,"Failed to get response");
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(CallSummary.this,
                        "Analysis failed: " + error, Toast.LENGTH_SHORT).show());
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
        if (sentimentText != null && analysis.getSentiment() != null) {
            String sentimentInfo = String.format("Sentiment: %s (%.2f%%)\n%s",
                    analysis.getSentiment().getLabel(),
                    analysis.getSentiment().getConfidence() * 100,
                    analysis.getSentiment().getExplanation());
            sentimentText.setText(sentimentInfo);
        } else {
            sentimentText.setText("No sentiment data");
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
            topicsText.setVisibility(View.GONE); // Hide if no topics
        }

        // Action Items
        if (!analysis.getActionItems().isEmpty()) {
            improvementInput.setText(String.join("\n• ", analysis.getActionItems()));
        }

        // Key Phrases
        if (!analysis.getKeyPhrases().isEmpty()) {
            feedbackInput.setText(String.join("\n• ", analysis.getKeyPhrases()));
        }

        // Questions
        if (!analysis.getQuestions().isEmpty()) {
            objectionsInput.setText(String.join("\n• ", analysis.getQuestions()));
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
