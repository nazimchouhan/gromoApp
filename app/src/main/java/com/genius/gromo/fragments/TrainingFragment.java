package com.genius.gromo.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.genius.gromo.model.ListItem;

import java.util.ArrayList;
import java.util.List;

public class TrainingFragment extends Fragment {
    private RecyclerView recyclerViewCalls;
    private CallsAdapter callsAdapter;


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
        
        // Create sample data with headers
        List<ListItem> items = createListItems();


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefsSummary", Context.MODE_PRIVATE);

        String recordingId=sharedPreferences.getString("recordingId",null);

        // Initialize adapter
        callsAdapter = new CallsAdapter(items, new CallsAdapter.OnViewMoreClickListener() {
            @Override
            public void onViewMoreClick(Call call) {
                Toast.makeText(requireContext(), "Viewing details for call with " + call.getName(), Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(getActivity(), CallSummary.class);
                intent.putExtra("recording_id", recordingId);
                startActivity(intent);

            }
        });
        
        recyclerViewCalls.setAdapter(callsAdapter);
    }

    private List<ListItem> createListItems() {
        List<ListItem> items = new ArrayList<>();
        List<Call> calls = getSampleCalls();
        
        String currentMonth = null;
        String currentDate = null;
        String currentYear = null;

        for (Call call : calls) {
            // If date changes, add a header
            if (currentMonth == null || 
                !currentMonth.equals(call.getMonth()) || 
                !currentDate.equals(call.getDate()) || 
                !currentYear.equals(call.getYear())) {
                
                currentMonth = call.getMonth();
                currentDate = call.getDate();
                currentYear = call.getYear();
                
                items.add(new ListItem(currentMonth, currentDate, currentYear));
            }
            
            items.add(new ListItem(call));
        }
        
        return items;
    }

    private List<Call> getSampleCalls() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefsSummary", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("userName", ""); // second param is default value
        String phoneNumber = sharedPreferences.getString("userPhone", "");
        String recordingId=sharedPreferences.getString("recordingId","");
        List<Call> calls = new ArrayList<>();
        calls.add(new Call(name, recordingId, "June", "15", "2024"));
        return calls;
    }
} 