package com.example.gromoapp;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LiveSummaryActivity extends AppCompatActivity {
    private TextView transcriptionText;
    private TextView sentimentScore;
    private boolean isMicMuted = false;
    private ProgressBar sentimentProgress;
    private RecyclerView remindersRecyclerView;
    private ReminderAdapter reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_summary);

        // Initialize views
        transcriptionText = findViewById(R.id.transcriptionText);
        sentimentScore = findViewById(R.id.sentimentScore);
        sentimentProgress = findViewById(R.id.sentimentProgress);
        remindersRecyclerView = findViewById(R.id.remindersRecyclerView);

        // Set up navigation buttons
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton closeButton = findViewById(R.id.closeButton);

        backButton.setOnClickListener(v -> onBackPressed());
        closeButton.setOnClickListener(v -> finish());

        ImageButton micButton = findViewById(R.id.micButton);
        ImageButton volumeButton = findViewById(R.id.volumeButton);

        micButton.setOnClickListener(this::onMicButtonClick);
        volumeButton.setOnClickListener(this::onVolumeButtonClick);

        // Set up RecyclerView for reminders
        setupRemindersRecyclerView();

        // Initialize with sample data
        updateTranscription("Agent: Hi, this is Sarah from customer support. How can I help you today?\n\nCustomer: I'm having trouble with my account.");
        updateSentiment(65);
        updateReminders();
    }

    private void setupRemindersRecyclerView() {
        reminderAdapter = new ReminderAdapter(new ArrayList<>());
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        remindersRecyclerView.setAdapter(reminderAdapter);
    }

    private void updateTranscription(String text) {
        transcriptionText.setText(text);
    }

    private void updateSentiment(int score) {
        sentimentScore.setText(String.valueOf(score));
        sentimentProgress.setProgress(score);
    }

    private void updateReminders() {
        List<Reminder> reminders = new ArrayList<>();
        reminders.add(new Reminder("Product Information", "Document"));
        reminders.add(new Reminder("Troubleshooting Guide", "Document"));
        reminderAdapter.updateReminders(reminders);
    }



    private void onMicButtonClick(View view) {
        // Handle mic button click

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null) {
            isMicMuted = !isMicMuted; // Toggle mute/unmute
            audioManager.setMicrophoneMute(isMicMuted);

            String status = isMicMuted ? "Muted" : "Unmuted";
            Toast.makeText(this, "Microphone " + status, Toast.LENGTH_SHORT).show();
        }

    }

    private void onVolumeButtonClick(View view) {
        // Handle volume button click
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(true);
            Toast.makeText(this, "Speakerphone ON", Toast.LENGTH_SHORT).show();
        }
    }
} 