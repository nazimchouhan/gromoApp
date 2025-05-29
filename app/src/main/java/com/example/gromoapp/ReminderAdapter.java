package com.example.gromoapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminders;

    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.titleView.setText(reminder.getTitle());
        holder.typeView.setText(reminder.getType());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void updateReminders(List<Reminder> newReminders) {
        this.reminders = newReminders;
        notifyDataSetChanged();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView typeView;

        ReminderViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(android.R.id.text1);
            typeView = itemView.findViewById(android.R.id.text2);
        }
    }
} 