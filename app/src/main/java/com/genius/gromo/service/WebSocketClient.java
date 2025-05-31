package com.genius.gromo.service;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public abstract class WebSocketClient {
    private OkHttpClient client;
    private WebSocket webSocket;
    private static final String WEBSOCKET_URL = "wss://262f-14-194-2-90.ngrok-free.app/ws";

    // Connection state
    private enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        RECONNECTING
    }
    private ConnectionState currentState = ConnectionState.DISCONNECTED;
    private final Object stateLock = new Object();

    // Reconnect
    private int reconnectAttempts = 0;
    private final int MAX_RECONNECT_ATTEMPTS = 200;
    private Timer reconnectTimer;

    // Keep-alive
    private Timer keepAliveTimer;
    private final long KEEP_ALIVE_INTERVAL = 20000; // 30 seconds
    private final long PING_TIMEOUT = 5000; // 10 seconds to wait for pong
    private boolean pongReceived = false;

    // Abstract method for message handling
    public void onMessageReceived(JSONObject message){};

    public void startWebSocket() {
        synchronized (stateLock) {
            if (currentState == ConnectionState.CONNECTED || currentState == ConnectionState.CONNECTING) {
                Log.d(TAG, "WebSocket already connected or connecting");
                return;
            }
            currentState = ConnectionState.CONNECTING;
        }

        Log.d(TAG, "Starting WebSocket...");
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }

        client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .pingInterval(10, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(WEBSOCKET_URL)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Opened. Response: " + response);
                synchronized (stateLock) {
                    currentState = ConnectionState.CONNECTED;
                    reconnectAttempts = 0;
                }
                JSONObject json = new JSONObject();

                try {
                    json.put("event", "start");
                    json.put("client_type", "frontend");
                    json.put("client_id", "123456789");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                String jsonString = json.toString();
                webSocket.send(jsonString);
                webSocket.send(jsonString);
//                startKeepAlive();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);
                if (text != null && (text.trim().startsWith("{") || text.trim().startsWith("["))) {
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        String type = jsonObject.optString("event");

                        if ("start".equals(type) || type.equals("ping")) {
                            pongReceived = true;
                        } else {
                            String temp_text = jsonObject.optString("content");

                            // Extract inner JSON
                            Log.d("sj", temp_text);
                            int start = temp_text.indexOf('{');
                            int end = temp_text.lastIndexOf('}') + 1;
                            Log.d("start " + start, "");
                            Log.d("end " + end, "");
                            if (start != -1 && end != -1 && end > start) {
                                String innerJsonStr = temp_text.substring(start, end);
                                Log.d("okd", innerJsonStr);
                                JSONObject innerJson = new JSONObject(innerJsonStr);

                                Log.d("Parsed inner JSON:\n", innerJson.toString(2));
                                onMessageReceived(innerJson);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing message: " + e.getMessage());
                    }
                } else {
                    Log.w("NON_JSON", "Received non-JSON text: " + text);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "Received bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closing: Code=" + code + ", Reason=" + reason);
                synchronized (stateLock) {
                    currentState = ConnectionState.DISCONNECTED;
                }
//                stopKeepAlive();
                webSocket.close(1000, null);
                attemptReconnect();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket Closed: Code=" + code + ", Reason=" + reason);
                synchronized (stateLock) {
                    currentState = ConnectionState.DISCONNECTED;
                }
//                stopKeepAlive();
                attemptReconnect();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Failure: " + t.getMessage());
                if (response != null) {
                    Log.e(TAG, "Failure Response: " + response);
                }
                synchronized (stateLock) {
                    currentState = ConnectionState.DISCONNECTED;
                }
//                stopKeepAlive();
                attemptReconnect();
            }
        });
    }

    private void attemptReconnect() {
        synchronized (stateLock) {
            if (currentState == ConnectionState.RECONNECTING) {
                Log.d(TAG, "Reconnection already in progress");
                return;
            }
            currentState = ConnectionState.RECONNECTING;
        }

        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }

        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
//            long delay = (long) Math.pow(2, reconnectAttempts) * 1000;
//            Log.d(TAG, "Attempt " + reconnectAttempts + ": Reconnecting in " + delay / 1000 + " seconds...");
//
            reconnectTimer = new Timer();
            reconnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "Reconnecting now...");
                    stopWebSocket();
                    startWebSocket();
                }
            }, 2000);
        } else {
            Log.e(TAG, "Max reconnect attempts reached. Giving up!");
            synchronized (stateLock) {
                currentState = ConnectionState.DISCONNECTED;
            }
        }
    }

//    private void startKeepAlive() {
//        if (keepAliveTimer != null) {
//            keepAliveTimer.cancel();
//        }
//        keepAliveTimer = new Timer();
//        Log.d(TAG, "Starting keep-alive ping every " + KEEP_ALIVE_INTERVAL / 1000 + " seconds.");
//
//        keepAliveTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (webSocket != null) {
//                    pongReceived = false;
//                    String pingMessage = "{\"event\":\"ping\"}";
//                    boolean sent = webSocket.send(pingMessage);
//
//                    if (sent) {
////                        Log.d(TAG, "Keep-alive ping sent successfully");
//                        new Timer().schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                if (!pongReceived) {
//                                    Log.w(TAG, "No pong received within timeout");
//                                    stopKeepAlive();
//                                    synchronized (stateLock) {
//                                        if (currentState == ConnectionState.CONNECTED) {
//                                            attemptReconnect();
//                                        }
//                                    }
//                                }
//                            }
//                        }, PING_TIMEOUT);
//                    } else {
//                        Log.w(TAG, "Failed to send keep-alive ping");
//                        stopKeepAlive();
//                        attemptReconnect();
//                    }
//                } else {
//                    Log.w(TAG, "WebSocket is null! Can't send ping.");
//                    stopKeepAlive();
//                    attemptReconnect();
//                }
//            }
//        }, KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL);
//    }
//
//    private void stopKeepAlive() {
//        if (keepAliveTimer != null) {
//            Log.d(TAG, "Stopping keep-alive ping.");
//            keepAliveTimer.cancel();
//            keepAliveTimer = null;
//        }
//    }

    public void stopWebSocket() {
        Log.d(TAG, "Stopping WebSocket.");
        synchronized (stateLock) {
            currentState = ConnectionState.DISCONNECTED;
        }
//        stopKeepAlive();
        
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
            reconnectTimer = null;
        }
        
        if (webSocket != null) {
            webSocket.close(1000, "Client closed");
            webSocket = null;
        }
        
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client = null;
        }
    }

    public boolean isConnected() {
        synchronized (stateLock) {
            return currentState == ConnectionState.CONNECTED;
        }
    }
}

