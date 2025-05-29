package com.genius.gromo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.SystemClock;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gromoapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallingActivity extends AppCompatActivity {

    private static final String TAG = "CallingActivity";
    private static final String API_KEY = "e00151d6919d8e3466b05530730b92e47cf93e0e04ddff77";
    private static final String API_TOKEN = "70a42e01c3d0526b6eb5823b699e2365d2157c5ed296cfcb";
    private static final String ACCOUNT_SID = "textrai1";
    private static final String BASE_URL = "https://ccm-api.exotel.com/v3/accounts/" + ACCOUNT_SID;

    private static final String CALLBACK_URL = "https://77e6-103-101-100-242.ngrok-free.app/exotel/webhook";

    private EditText phoneNumberInput;

    private TextInputLayout phoneNumberLayout;
    private MaterialButton callButton;
    private MaterialButton callSummaryButton;
    private TextView callStatusText;
    private Chronometer callDurationTimer;
    private OkHttpClient client;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private boolean isCallActive = false;
    long duration;
    private String recordingId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        callSummaryButton=findViewById(R.id.CallSummaryButton);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
        phoneNumberLayout = findViewById(R.id.phoneNumberLayout);
        callButton = findViewById(R.id.callButton);
        callStatusText = findViewById(R.id.callStatusText);
        callDurationTimer = findViewById(R.id.callDurationTimer);

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        setupPhoneNumberValidation();
        setupCallButton();
        callSummaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), CallSummary.class);
                startActivity(intent);
            }
        });

    }
    private String getBasicAuthHeader() {
        String credentials = API_KEY + ":" + API_TOKEN;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }


    private void setupPhoneNumberValidation() {
        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString().trim();
                boolean isValid = isValidPhoneNumber(phoneNumber);
                phoneNumberLayout.setError(isValid ? null : "Invalid phone number");
                callButton.setEnabled(isValid);
                callButton.setBackgroundTintList(ContextCompat.getColorStateList(
                        CallingActivity.this,
                        isValid ? R.color.primary_blue : android.R.color.darker_gray
                ));
            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.length() == 10 && phoneNumber.matches("\\d{10}");
    }

    private void setupCallButton() {
        callButton.setOnClickListener(v -> {
            makePhoneCall();
        });
    }

    private void makePhoneCall() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        startCallRecording("7814001336", phoneNumber);
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
            json.put("virtual_number", "08047092789");
            json.put("recording", recordingJson);
            json.put("max_time_limit", 4000);
            json.put("attempt_time_out", 45);
            json.put("custom_field", "bilbo_test_call");

            JSONObject callbackObj = new JSONObject();
            callbackObj.put("event", "terminal");
            callbackObj.put("url", CALLBACK_URL);

            JSONArray callbackArray = new JSONArray();
            callbackArray.put(callbackObj);

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
                    if (response.isSuccessful()) {
                        isCallActive=true;
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("responseId", responseBody);
                        editor.apply();
                        Log.e(TAG, "Call started successfully: " + responseBody);
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
}

