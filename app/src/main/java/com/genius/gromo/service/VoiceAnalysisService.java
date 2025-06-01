package com.genius.gromo.service;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import okhttp3.*;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class VoiceAnalysisService {
    private static final String BASE_URLRecording = "https://bb0c-14-194-2-90.ngrok-free.app"; // consider this line

    private final Context context;
    private final OkHttpClient client;

    public VoiceAnalysisService(Context context) {
        this.context = context;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public void analyzeRecording(String recordingId, final ApiCallback callback) {
        Request request = new Request.Builder() // consider this line
                .url(BASE_URLRecording + "/recordings/" + recordingId + "/analysis") //check this line
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                callback.onFailure(e.getMessage());
                Log.e(TAG,"Failed to Call the Request");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if(response.body()!=null){
                        String responseData = response.body().string();
                        Log.e("responsedata",responseData);
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            callback.onSuccess(jsonObject);
                        }catch (org.json.JSONException e) {
                            e.printStackTrace();
                            callback.onFailure("JSON parsing error: " + e.getMessage());
                        }
                    }
                }else{
                    callback.onFailure("Analysis failed: " + response.code());
                    Log.e(TAG,"Response not Received");
                }
            }
        });
    }

    public interface ApiCallback {
        void onSuccess(JSONObject result);
        void onFailure(String error);
    }
}
