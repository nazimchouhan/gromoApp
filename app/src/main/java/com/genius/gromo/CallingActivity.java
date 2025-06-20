package com.genius.gromo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gromoapp.LiveSummaryActivity;
import com.example.gromoapp.R;
import com.genius.gromo.service.LiveCallWebSocketClient;
import com.genius.gromo.service.WebSocketClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class CallingActivity extends AppCompatActivity {

    private static final String TAG = "CallingActivity";
    private static final String API_KEY = "0a22d12dfc8b8d8a1792f1ed7e88f889b86b6964fc86c763";
    private static final String API_TOKEN = "443692cd141628915527d683d571ff6b28f55c07f03a8c02";
    private static final String ACCOUNT_SID = "itbhu4";
    private static final String BASE_URL = "https://ccm-api.exotel.com/v3/accounts/" + ACCOUNT_SID;

    private static final String WEBSOCKET_URL = "wss://a5b7-14-194-2-90.ngrok-free.app/ws";
    private static final String CALLBACK_URL = "https://bb0c-14-194-2-90.ngrok-free.app/exotel/webhook";

    private AutoCompleteTextView phoneNumberInput;
    private AutoCompleteTextView nameInput;
    private String name = "";
    private String phoneNumber = "";
    private TextInputLayout nameInputLayout;

    private TextInputLayout phoneNumberLayout;
    private MaterialButton callButton;
    private MaterialButton callSummaryButton;
    private TextView callStatusText;
    private Chronometer callDurationTimer;
    private OkHttpClient client;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private boolean isCallActive = false;
    private LiveCallWebSocketClient webSocketClient;
    long duration;
    private String recordingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        initializeViews();

        String[] names = {"NITIN 7015219603", "NAZIM 8058086931", "HARPREET 9653040310"};
//        String[] numbers = {"7015219603", "8058086931", "9653040310"};
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);

        nameInput.setAdapter(nameAdapter);
//        phoneNumberInput.setAdapter(numberAdapter);
        nameInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                nameInput.showDropDown();
            }
        });

        nameInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            String[] parts = selectedName.trim().split("\\s+");
            name = parts[0];
            phoneNumber = parts[1];
            Log.d("NameSelection", name+ " " + phoneNumber);
            callButton.setEnabled(true);
            callButton.setBackgroundTintList(ContextCompat.getColorStateList(
                    this, R.color.primary_blue));
            Toast.makeText(this, "You selected: " + selectedName, Toast.LENGTH_SHORT).show();
        });
//        phoneNumberInput.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                phoneNumberInput.showDropDown();
//            }
//        });
        
        // Initialize views
        
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

//        setupInputValidation();
//        setupWebSocket();
        setupCallButton();
//        callButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getApplicationContext(), LiveSummaryActivity.class);
//                startActivity(intent);
//            }
//        });

//        callSummaryButton.setOnClickListener(v -> {
//            Log.e("","ActivityStarted");
//            Intent intent = new Intent(getApplicationContext(), CallSummary.class);
//            startActivity(intent);
//        });
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.NameInput);
//        nameInputLayout = findViewById(R.id.NameLayout);
        //callSummaryButton = findViewById(R.id.CallSummaryButton);
//        phoneNumberInput = findViewById(R.id.phoneNumberInput);
//        phoneNumberLayout = findViewById(R.id.phoneNumberLayout);
        callButton = findViewById(R.id.callButton);
//        callStatusText = findViewById(R.id.callStatusText);
//        callDurationTimer = findViewById(R.id.callDurationTimer);

         //Disable call button initially
        if (callButton != null) {
            callButton.setEnabled(false);
            callButton.setBackgroundTintList(ContextCompat.getColorStateList(
                    this, android.R.color.darker_gray));
        }
    }

//    private void setupInputValidation() {
//        if (phoneNumberInput != null) {
//            phoneNumberInput.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    validateInputs();
//                }
//            });
//        }
//
//        if (nameInput != null) {
//            nameInput.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    validateInputs();
//                }
//            });
//        }
//    }

    private void validateInputs() {
//        String phoneNumber = phoneNumberInput != null ? phoneNumberInput.getText().toString().trim() : "";
//        String name = nameInput != null ? nameInput.getText().toString().trim() : "";
//
//        boolean isNameValid = isValidName(name);
//        boolean isPhoneValid = isValidPhoneNumber(phoneNumber);
//
//        if (nameInputLayout != null) {
//            nameInputLayout.setError(isNameValid ? null : "Invalid name (only A-Z)");
//        }
//        if (phoneNumberLayout != null) {
//            phoneNumberLayout.setError(isPhoneValid ? null : "Invalid phone number");
//        }
//
//        boolean isFormValid = isNameValid && isPhoneValid;
//
//        if (callButton != null) {
//            callButton.setEnabled(isFormValid);
//            callButton.setBackgroundTintList(ContextCompat.getColorStateList(
//                    CallingActivity.this,
//                    isFormValid ? R.color.primary_blue : android.R.color.darker_gray
//            ));
//        }

        // Record user details only if both inputs are valid
//        if (isFormValid) {
            recordUserDetails(name, phoneNumber);
//        }
    }

//    private void setupWebSocket() {
//        webSocketClient = new LiveCallWebSocketClient(this, this);
//        webSocketClient.startWebSocket();
//    }
//
//    @Override
//    public void onMessageReceived(JSONObject message) {
//        runOnUiThread(() -> {
////            try {
////                JSONObject jsonMessage = new JSONObject(message);
//                // Handle the WebSocket message
//                // You can update UI or handle different message types here
//                Log.d(TAG, "Received WebSocket message: " + message);
////            } catch (JSONException e) {
////                Log.e(TAG, "Error parsing WebSocket message", e);
////            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (webSocketClient != null) {
//            webSocketClient.stopWebSocket();
//        }
//    }

    private String getBasicAuthHeader() {
        String credentials = API_KEY + ":" + API_TOKEN;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    private boolean isValidName(String name) {
        return name.matches("[A-Z]{1,10}"); // Only uppercase letters, max 10
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.length() == 10 && phoneNumber.matches("\\d{10}");
    }

    private void setupCallButton() {
        callButton.setOnClickListener(v -> {
//            String phoneNumber = phoneNumberInput.getText().toString().trim();
//            String name = nameInput.getText().toString().trim();
            
            if (phoneNumber.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            makePhoneCall();
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute(() -> {
//                makePhoneCall();
//            });

//             //Start LiveSummaryActivity
//            Intent intent = new Intent(getApplicationContext(), LiveSummaryActivity.class);
//            startActivity(intent);

            // Start the call recording

        });
    }

    private void startCallSummaryActivity() {
        Intent intent = new Intent(getApplicationContext(), CallSummary.class);
        if (recordingId != null && !recordingId.isEmpty()) {
            intent.putExtra("recording_id", recordingId);
        }
        startActivity(intent);
    }

    private void makePhoneCall() {
//        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        startCallRecording("8058086931", phoneNumber);
        // Simulate starting call recording
        // look into this method if any problem occur
    }

    private void startCallRecording(String fromNumber, String toNumber) {
        try {

            JSONObject json = new JSONObject();

            JSONObject fromJson = new JSONObject();
            fromJson.put("contact_uri", fromNumber);
            fromJson.put("state_management", true);

            JSONObject toJson = new JSONObject();
            toJson.put("contact_uri", toNumber);

            JSONObject recordingJson = new JSONObject();
            recordingJson.put("record", true);
            recordingJson.put("channels", "single");

            json.put("from", fromJson);
            json.put("to", toJson);
            json.put("virtual_number", "08047092858");
            json.put("recording", recordingJson);
            json.put("max_time_limit", 4000);
            json.put("attempt_time_out", 45);
            json.put("custom_field", "bilbo_test_call");

            JSONObject callbackObj = new JSONObject();
            callbackObj.put("event", "terminal");
            callbackObj.put("url", CALLBACK_URL);

            JSONArray callbackArray = new JSONArray();
            callbackArray.put(callbackObj);

            JSONObject streamingObj = new JSONObject();
            streamingObj.put("url", WEBSOCKET_URL);
            streamingObj.put("begin", "to_leg_connect");

            json.put("streaming",streamingObj);

            json.put("status_callback", callbackArray);

            String jsonString = json.toString();
            //RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
            RequestBody body = RequestBody.create(jsonString.getBytes("UTF-8"), null);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/calls")
                    .addHeader("Authorization", getBasicAuthHeader())
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to start call recording", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    Log.e("kjid", responseBody);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONObject responseObj = jsonResponse.getJSONObject("response");
                            JSONObject callDetails = responseObj.getJSONObject("call_details");
                            String sid = callDetails.getString("sid");  // 🎯 Extracted SID
                            Log.e("SID",sid);
                            recordingId=sid;
                            // Save the SID
                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("recordingId", sid);  // Using 'recordingId' to save the sid
                            editor.apply();

//                            String phoneNumber = phoneNumberInput.getText().toString().trim();
//                            String name = nameInput.getText().toString().trim();
                            recordUserDetails(name, phoneNumber);

                            Log.e(TAG, "Call started successfully. SID: " + sid);

                            //Start LiveSummaryActivity
                            Intent intent = new Intent(getApplicationContext(), LiveSummaryActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response JSON", e);
                        }
                    } else {
                        Log.e(TAG, "Failed response: " + response.code() + " - " + response.message());
                        Log.e(TAG, "Response body: " + responseBody);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error starting call recording", e);
        }
    }

    private void recordUserDetails(String name, String phoneNumber) {
        Log.d("UserDetails", "Name: " + name + ", Phone: " + phoneNumber);
        SharedPreferences prefs = getSharedPreferences("UserPrefsSummary", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        // Example: Save in shared preferences (or any other method)
        JSONArray namesArray = new JSONArray();
        JSONArray recordingsArray = new JSONArray();

        // Load existing data
        try {
            String namesString = prefs.getString("userNames", "[]");
            Log.d("Stored names", namesString);
            namesArray = new JSONArray(namesString);
        } catch (JSONException e) {
            Log.e("SharedPrefs", "Failed to parse userNames: " + e.getMessage());
        }

        try {
            String recordingsString = prefs.getString("recordingIds", "[]");
            Log.d("Stored recordings", recordingsString);
            recordingsArray = new JSONArray(recordingsString);
        } catch (JSONException e) {
            Log.e("SharedPrefs", "Failed to parse recordingIds: " + e.getMessage());
        }

        // Add new data
        namesArray.put(name);
        recordingsArray.put(recordingId);

        // Save back to SharedPreferences
        editor.putString("userNames", namesArray.toString());
        editor.putString("recordingIds", recordingsArray.toString());
        editor.apply();
    }
}