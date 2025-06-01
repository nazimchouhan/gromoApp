package com.genius.gromo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gromoapp.R;
import com.example.gromoapp.adapters.CallsAdapter;
import com.genius.gromo.CallSummary;
import com.genius.gromo.model.Call;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class TrainingFragment extends Fragment {
    private RecyclerView recyclerViewCalls;
    private CallsAdapter callsAdapter;
    private List<Call> callList;


    public TrainingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerViewCalls = view.findViewById(R.id.recyclerViewCalls);
        recyclerViewCalls.setLayoutManager(new LinearLayoutManager(requireContext()));

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefsSummary", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String names = sharedPreferences.getString("userNames", "[]");
        String recordingIds = sharedPreferences.getString("recordingIds","[]");
        Log.d(names, recordingIds);
        JSONArray namesArray = new JSONArray();
        JSONArray recordingsArray = new JSONArray();
        callList = new ArrayList<>();

        try {
            if (names != null && !names.isEmpty()) {
                namesArray = new JSONArray(names);
            }
        } catch (JSONException e) {
            Log.e("CallListBuilder", "Failed to parse names JSON: " + e.getMessage());
        }

        try {
            if (recordingIds != null && !recordingIds.isEmpty()) {
                recordingsArray = new JSONArray(recordingIds);
            }
        } catch (JSONException e) {
            Log.e("CallListBuilder", "Failed to parse names JSON: " + e.getMessage());
        }

        int size = Math.min(namesArray.length(), recordingsArray.length());
        for (int i = 0; i < size; i++) {
            String name = null;
            try {
                name = namesArray.getString(i);
            } catch (JSONException e) {
                Log.e("CallListBuilder", "Failed to parse names JSON: " + e.getMessage());
            }
            String recordingId = null;
            try {
                recordingId = recordingsArray.getString(i);
            } catch (JSONException e) {
                Log.e("CallListBuilder", "Failed to parse names JSON: " + e.getMessage());
            }

            callList.add(new Call(name, recordingId));
        }
        callsAdapter = new CallsAdapter(getActivity(), callList);
        recyclerViewCalls.setAdapter(callsAdapter);
    }
} 