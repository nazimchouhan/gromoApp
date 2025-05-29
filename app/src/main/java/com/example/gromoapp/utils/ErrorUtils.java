package com.example.gromoapp.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

public class ErrorUtils {
    private static final String TAG = "ErrorUtils";

    public static void handleActivityError(Context context, String activity, Exception e) {
        Log.e(TAG, "Error launching activity: " + activity, e);
        showErrorDialog(context, "Navigation Error", 
            "Unable to open the requested screen. Please try again.");
    }

    public static void handleNetworkError(Context context, String operation, Exception e) {
        Log.e(TAG, "Network error during: " + operation, e);
        showErrorDialog(context, "Network Error", 
            "Unable to connect to the server. Please check your internet connection.");
    }

    public static void handlePermissionError(Context context, String permission) {
        Log.e(TAG, "Permission denied: " + permission);
        showErrorDialog(context, "Permission Required", 
            "This feature requires " + permission + " permission to work properly.");
    }

    public static void handleGeneralError(Context context, String operation, Exception e) {
        Log.e(TAG, "Error during: " + operation, e);
        showErrorDialog(context, "Error", 
            "An unexpected error occurred. Please try again.");
    }

    public static void showErrorDialog(Context context, String title, String message) {
        try {
            new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setNegativeButton("Report", (dialog, which) -> {
                    // TODO: Implement error reporting
                    Toast.makeText(context, "Error reported", Toast.LENGTH_SHORT).show();
                })
                .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing error dialog", e);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isActivityResolvable(Context context, Intent intent) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }
} 