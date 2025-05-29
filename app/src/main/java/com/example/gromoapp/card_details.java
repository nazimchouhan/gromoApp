package com.example.gromoapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.genius.gromo.CallingActivity;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class card_details extends AppCompatActivity {
    MaterialButton SellButton;
    private OkHttpClient client;
    private static final String API_KEY = "e00151d6919d8e3466b05530730b92e47cf93e0e04ddff77";
    private static final String API_TOKEN = "70a42e01c3d0526b6eb5823b699e2365d2157c5ed296cfcb";
    private static final String ACCOUNT_SID = "textrai1";
    private static final String BASE_URL = "https://ccm-api.exotel.com/v3/accounts/" + ACCOUNT_SID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        SellButton = findViewById(R.id.SellButton);
        
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        SellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startCallRecording("7814001336", "7015219603");
                Intent intent = new Intent(getApplicationContext(), CallingActivity.class);
                startActivity(intent);
            }
        });
    }

//    private String getBasicAuthHeader() {
//        String credentials = API_KEY + ":" + API_TOKEN;
//        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//    }
//
//    private void startCallRecording(String fromNumber, String toNumber) {
//        try {
//            // Construct the JSON payload
//            JSONObject json = new JSONObject();
//            JSONObject fromJson = new JSONObject();
//            fromJson.put("contact_uri", fromNumber);
//            fromJson.put("state_management", true);
//
//            JSONObject toJson = new JSONObject();
//            toJson.put("contact_uri", toNumber);
//
//            JSONObject recordingJson = new JSONObject();
//            recordingJson.put("record", true);
//            recordingJson.put("channels", "single");
//
//            json.put("from", fromJson);
//            json.put("to", toJson);
//            json.put("recording", recordingJson);
//            json.put("virtual_number", "08047092789");
//            json.put("max_time_limit", 4000);
//            json.put("attempt_time_out", 45);
//            json.put("custom_field", "bilbo_test_call");
//
//            String jsonString = json.toString();
//            Log.e(TAG, "Request body: " + jsonString);
//
//            // Create request body with explicit content type
//            RequestBody requestBody = RequestBody.create(jsonString.getBytes("UTF-8"), null);
//
//            // Build the request with manual content type header
//            Request request = new Request.Builder()
//                    .url(BASE_URL + "/calls")
//                    .header("Authorization", getBasicAuthHeader())
//                    .header("Content-Type", "application/json")
//                    .post(requestBody)
//                    .build();
//
//            Log.e(TAG, "Request URL: " + request.url());
//            Log.e(TAG, "Request Headers: " + request.headers());
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.e(TAG, "Failed to start recording", e);
//                    runOnUiThread(() -> {
//                        Toast.makeText(card_details.this, "Failed to initiate call", Toast.LENGTH_SHORT).show();
//                    });
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String responseBody = response.body() != null ? response.body().string() : "No response body";
//                    Log.e(TAG, "Response Headers: " + response.headers());
//
//                    if (response.isSuccessful()) {
//                        Log.e(TAG, "Success response: " + responseBody);
//                        try {
//                            JSONObject respJson = new JSONObject(responseBody);
//                            Log.e(TAG, "Call initiated successfully: " + respJson.toString());
//                            runOnUiThread(() -> {
//                                Toast.makeText(card_details.this, "Call initiated successfully", Toast.LENGTH_SHORT).show();
//                            });
//                        } catch (Exception e) {
//                            Log.e(TAG, "Error parsing response", e);
//                        }
//                    } else {
//                        Log.e(TAG, "Failed response: " + response.code() + " - " + response.message());
//                        Log.e(TAG, "Response body: " + responseBody);
//                        runOnUiThread(() -> {
//                            Toast.makeText(card_details.this,
//                                "Failed to initiate call: " + response.code(),
//                                Toast.LENGTH_SHORT).show();
//                        });
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "Error creating request", e);
//            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
//        }
//    }
}