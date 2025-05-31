package com.example.gromoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gromoapp.R;
import com.genius.gromo.model.Call;
import com.genius.gromo.model.ListItem;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class CallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> items;
    private OnViewMoreClickListener onViewMoreClickListener;

    public interface OnViewMoreClickListener {
        void onViewMoreClick(Call call);
    }

    public CallsAdapter(List<ListItem> items, OnViewMoreClickListener listener) {
        this.items = items;
        this.onViewMoreClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_call, parent, false);
            return new CallViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof CallViewHolder) {
            ((CallViewHolder) holder).bind(item.getCall());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView month;
        private TextView date;
        private TextView year;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.tvHeaderMonth);
            date = itemView.findViewById(R.id.tvHeaderDate);
            year = itemView.findViewById(R.id.tvHeaderYear);
        }

        public void bind(ListItem item) {
            month.setText(item.getHeaderMonth());
            date.setText(item.getHeaderDate() + ",");
            year.setText(item.getHeaderYear());
        }
    }

    class CallViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView time;
        private MaterialButton btnViewMore;

        public CallViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvTitle);
            time = itemView.findViewById(R.id.tvTime);
            btnViewMore = itemView.findViewById(R.id.btnViewMore);
        }

        public void bind(final Call call) {
            name.setText("Call with " + call.getName());
            time.setText(call.getTime());
            
            btnViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewMoreClickListener.onViewMoreClick(call);
                }
            });
        }
    }
}

