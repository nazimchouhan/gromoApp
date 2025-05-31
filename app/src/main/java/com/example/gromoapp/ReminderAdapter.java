//package com.example.gromoapp;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
//
//    private List<String> reminders;
//
//    public ReminderAdapter(List<String> reminders) {
//        this.reminders = reminders;
//    }
//
//    @NonNull
//    @Override
//    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // Inflate the item layout from XML
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_reminder, parent, false);
//        return new ReminderViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
//        String reminder = reminders.get(position);
//        holder.titleView.setText(reminder);
//    }
//
//    @Override
//    public int getItemCount() {
//        return reminders.size();
//    }
//
//    public void updateReminders(List<String> newReminders) {
//        this.reminders = newReminders;
//        notifyDataSetChanged();
//    }
//
//    static class ReminderViewHolder extends RecyclerView.ViewHolder {
//        TextView titleView;
//
//        ReminderViewHolder(View itemView) {
//            super(itemView);
//            titleView = itemView.findViewById(R.id.reminder_text);
//        }
//    }
//}
