package com.example.wapamwatchdog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder> {

    private List<String> items;

    public LocationsAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_locations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.textViewTitle.setText(item);
        holder.textViewTitle.setOnClickListener(v -> {
            if (holder.textViewDetails.getVisibility() == View.GONE) {
                holder.textViewDetails.setVisibility(View.VISIBLE);
            } else {
                holder.textViewDetails.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
        }
    }
}

