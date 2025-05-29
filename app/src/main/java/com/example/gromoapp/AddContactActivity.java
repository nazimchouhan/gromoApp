package com.example.gromoapp;

import static android.app.ProgressDialog.show;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gromoapp.fragments.LeadsFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddContactActivity extends AppCompatActivity {
    private static final String BASE_URL="https://gurgoanproperties.in";
    private TextInputLayout mobileInputLayout;
    private TextInputLayout nameInputLayout;
    private TextInputLayout panInputLayout;
    private TextInputLayout dobInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout pinCodeInputLayout;

    private TextInputEditText mobileInput;
    private TextInputEditText nameInput;
    private TextInputEditText panInput;
    private TextInputEditText dobInput;
    private TextInputEditText emailInput;
    boolean isSubmit=false;
    private TextInputEditText pinCodeInput;
    private AutoCompleteTextView genderDropdown;

    private MaterialButton submitButton;
    private Toolbar toolbar;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize input layouts
        mobileInputLayout = findViewById(R.id.mobileInputLayout);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        panInputLayout = findViewById(R.id.panInputLayout);
        dobInputLayout = findViewById(R.id.dobInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        pinCodeInputLayout = findViewById(R.id.pinCodeInputLayout);

        // Initialize input fields
        mobileInput = findViewById(R.id.mobileInput);
        nameInput = findViewById(R.id.nameInput);
        panInput = findViewById(R.id.panInput);
        dobInput = findViewById(R.id.dobInput);
        emailInput = findViewById(R.id.emailInput);
        pinCodeInput = findViewById(R.id.pinCodeInput);
        genderDropdown = findViewById(R.id.genderDropdown);

        String[] genderOptions = {"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        genderDropdown.setAdapter(adapter);
        dobInput.setOnClickListener(v -> showDatePicker());
        // Initialize button
        submitButton = findViewById(R.id.continueButton);

    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String dob = year1 + "-" + String.format("%02d", month1 + 1) + "-" + String.format("%02d", dayOfMonth);
                    dobInput.setText(dob);
                }, year, month, day);
        dialog.show();
    }


    private void setupListeners() {
        // Add text change listeners
        mobileInput.addTextChangedListener(new ValidationTextWatcher(mobileInput));
        nameInput.addTextChangedListener(new ValidationTextWatcher(nameInput));
        panInput.addTextChangedListener(new ValidationTextWatcher(panInput));
        emailInput.addTextChangedListener(new ValidationTextWatcher(emailInput));
        pinCodeInput.addTextChangedListener(new ValidationTextWatcher(pinCodeInput));

        // Set up toolbar navigation
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Set up submit button click listener
        if (submitButton != null) {
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateAndSendData();
                    if(isSubmit==true){
                        isSubmit=false;
                        Intent intent=new Intent(getApplicationContext(), LeadsFragment.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(AddContactActivity.this, "Failed to Submit Detail,Please Try Again", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void validateAndSendData() {
        // Validate all fields
        if (nameInput.getText().toString().trim().isEmpty() ||
                mobileInput.getText().toString().trim().isEmpty() ||
                panInput.getText().toString().trim().isEmpty() ||
                dobInput.getText().toString().trim().isEmpty() ||
                emailInput.getText().toString().trim().isEmpty() ||
                pinCodeInput.getText().toString().trim().isEmpty() ||
                genderDropdown.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //JSONOArray userJson = new JSONObject();
        JSONArray userJson=new JSONArray();
        try {
            userJson.put( mobileInput.getText().toString().trim());
            userJson.put(nameInput.getText().toString().trim());
            userJson.put(panInput.getText().toString().trim());
            userJson.put(dobInput.getText().toString().trim());
            userJson.put(genderDropdown.getText().toString().trim());
            userJson.put(emailInput.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error building JSON", Toast.LENGTH_SHORT).show();
            return;
        }
        sendItemsToServer(userJson);
    }

    private void sendItemsToServer(JSONArray json) {

        // Build request body
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        // Build request

        Request request = new Request.Builder()
                .url(BASE_URL + "/customers")
                .post(body)
                .addHeader("Accept", "application/json")
                .build();

        // Create OkHttpClient
        OkHttpClient client = new OkHttpClient();

        // Make async call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(AddContactActivity.this,
                        "API call failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(AddContactActivity.this,
                            "API success: " + responseBody, Toast.LENGTH_SHORT).show());
                    isSubmit=true;
                } else {
                    runOnUiThread(() -> Toast.makeText(AddContactActivity.this,

                            "API error: " + responseBody, Toast.LENGTH_SHORT).show());
                    Log.e("body",responseBody);
                }
            }
        });
    }
    private class ValidationTextWatcher implements TextWatcher {
        private View view;

        public ValidationTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();

            if (view instanceof TextInputEditText) {
                TextInputEditText editText = (TextInputEditText) view;

                switch (view.getId()) {
                    case R.id.nameInput:
                        if (text.isEmpty()) {
                            editText.setError("Name is required");
                        } else {
                            editText.setError(null);
                        }
                        break;

                    case R.id.panInput:
                        String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
                        if (!text.matches(panPattern) && !text.isEmpty()) {
                            editText.setError("Invalid PAN format");
                        } else {
                            editText.setError(null);
                        }
                        break;

                    case R.id.emailInput:
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                        if (!text.matches(emailPattern) && !text.isEmpty()) {
                            editText.setError("Invalid email format");
                        } else {
                            editText.setError(null);
                        }
                        break;

                    case R.id.pinCodeInput:
                        if (text.length() != 6 && !text.isEmpty()) {
                            editText.setError("PIN code must be 6 digits");
                        } else {
                            editText.setError(null);
                        }
                        break;

                    case R.id.mobileInput:
                        if (text.length() != 10 && !text.isEmpty()) {
                            editText.setError("Mobile number must be 10 digits");
                        } else {
                            editText.setError(null);
                        }
                        break;
                }
            }
        }
    }
}
