package com.example.hw4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder> {
    private Context context;
    private List<PortfolioItem> portfolioItems;

    public PortfolioAdapter(Context context, List<PortfolioItem> portfolioItems) {
        this.context = context;
        this.portfolioItems = portfolioItems;
    }

    @Override
    public PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_main_layout, parent, false);
        return new PortfolioViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(PortfolioViewHolder holder, int position) {
        PortfolioItem item = portfolioItems.get(position);
        holder.symbolTextView.setText(item.getSymbol());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.totalCostTextView.setText(String.format("$%.2f", item.getFinalcost()));

        if (holder.changePortTextView != null) {
            double change = item.getChange();
            double changePercent = item.getChangePercent();
            holder.changePortTextView.setText(String.format(Locale.getDefault(), "%+.2f (%+.2f%%)", change, changePercent));

            if (change > 0) {
                holder.arrowImageView.setImageResource(R.drawable.trending_up);
                holder.changePortTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else if (change < 0) {
                holder.arrowImageView.setImageResource(R.drawable.trending_down);
                holder.changePortTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                holder.arrowImageView.setImageResource(R.drawable.trending_up); // Assuming you have a neutral icon
                holder.changePortTextView.setTextColor(ContextCompat.getColor(context, R.color.black)); // Define this color in your colors.xml
            }
        } else {
            Log.e("PortfolioAdapter", "changePortTextView is null");
        }

        holder.searchButton.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).performSearch(item.getSymbol());
            }
        });
    }

    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }

    public static class PortfolioViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView;
        TextView quantityTextView;
        TextView totalCostTextView;
        ImageButton searchButton;
        TextView changePortTextView;
        ImageView arrowImageView;

        public PortfolioViewHolder(View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            searchButton = itemView.findViewById(R.id.searchButton);
            totalCostTextView = itemView.findViewById(R.id.totalCostTextView);
            arrowImageView = itemView.findViewById(R.id.arrowImageView);
            changePortTextView = itemView.findViewById(R.id.changePortTextView);
        }
    }
}
