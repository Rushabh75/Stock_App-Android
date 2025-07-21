package com.example.hw4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> {
    private List<WatchlistItem> watchlistItems;
    private Context context;

    public WatchlistAdapter(Context context, List<WatchlistItem> watchlistItems) {
        this.context = context;
        this.watchlistItems = watchlistItems;
    }

    @NonNull
    @Override
    public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.watchlist_item_layout, parent, false);
        return new WatchlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
        WatchlistItem item = watchlistItems.get(position);
        holder.symbolTextView.setText(item.getSymbol());
        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.format("$%.2f", item.getCurrentPrice()));

        // Set color and image based on whether the change is positive or negative
        int color;
        int imageRes;
        if (item.getChange() >= 0) {
            color = R.color.green;
            imageRes = R.drawable.trending_up; // Positive change image
        } else {
            color = R.color.red;
            imageRes = R.drawable.trending_down; // Negative change image
        }
        holder.changeTextView.setTextColor(ContextCompat.getColor(context, color));
        holder.changeTextView.setText(String.format("%+.2f (%+.2f%%)", item.getChange(), item.getChangePercent()));
        holder.arrowImageView.setImageResource(imageRes); // Set the image resource

        // Set up the search button click listener
        holder.searchButton.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).performSearch(item.getSymbol());
            }
        });
    }

    @Override
    public int getItemCount() {
        return watchlistItems.size();
    }

    public static class WatchlistViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView;
        TextView nameTextView;
        TextView priceTextView;
        TextView changeTextView;
        ImageButton searchButton;
        ImageView arrowImageView;

        public WatchlistViewHolder(@NonNull View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            changeTextView = itemView.findViewById(R.id.changeTextView);
            searchButton = itemView.findViewById(R.id.searchButton);
            arrowImageView = itemView.findViewById(R.id.arrowImageView);
        }
    }
}
