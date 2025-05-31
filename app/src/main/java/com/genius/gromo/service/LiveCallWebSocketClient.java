package com.genius.gromo.service;

import android.content.Context;

import org.json.JSONObject;

public class LiveCallWebSocketClient extends WebSocketClient {
    private final Context context;
    private final MessageCallback callback;

    public interface MessageCallback {
        void onMessageReceived(JSONObject message);
    }

    public LiveCallWebSocketClient(Context context, MessageCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public void onMessageReceived(JSONObject message) {
        if (callback != null) {
            callback.onMessageReceived(message);
        }

    }
} 