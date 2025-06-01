package com.genius.gromo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        String name = sharedPreferences.getString("userName", "Nitin");
        String recordingId=sharedPreferences.getString("recordingId","cae6b462456c51dd3c9e9f3173a11961");
        callList=new ArrayList<>();
        callList.add(new Call(name,recordingId));
        callsAdapter = new CallsAdapter(getActivity(), callList);
        recyclerViewCalls.setAdapter(callsAdapter);

    }
} 