package com.example.gromoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

//import com.example.gromoapp.CallAnalysisActivity;
import com.example.gromoapp.R;
import com.example.gromoapp.credit_card;

public class GroMoFragment extends Fragment {
    CardView gromo_credit_card;
    CardView vehicleInsurance;
    CardView carInsurance;
    public GroMoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_gro_mo, container, false);
        gromo_credit_card=view.findViewById(R.id.gromo_credit_card);
        vehicleInsurance=view.findViewById(R.id.vehicleinsurance);
        carInsurance=view.findViewById(R.id.carinsurance);
        gromo_credit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), credit_card.class);
                startActivity(intent);
            }
        });
        /*vehicleInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CallAnalysisActivity.class);
                startActivity(intent);
            }
        });
        carInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CallAnalysisActivity.class);
                startActivity(intent);
            }
        });*/
        return view;
    }
}