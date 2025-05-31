package com.example.gromoapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gromoapp.R;
import com.genius.gromo.CallSummary;
import com.genius.gromo.model.Call;

import java.util.List;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.CallViewHolder> {

    private List<Call> items;
    private Context context;

    public CallsAdapter(Context context, List<Call> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_call, parent, false);
        return new CallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        Call call = items.get(position);
        holder.nameTextView.setText(call.getName());
        holder.recordingIdTextView.setText(call.getRecordingId());
        holder.itemView.setOnClickListener(v -> {
            // Using the getter methods to retrieve the name and recordingId
            String recordingId = call.getRecordingId();
            if(recordingId!=null){
                Intent intent =new Intent(context, CallSummary.class);
                intent.putExtra("recordingId",recordingId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder class
    public static class CallViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView recordingIdTextView;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvTitle);
            recordingIdTextView = itemView.findViewById(R.id.tvTime);
        }
    }
}
