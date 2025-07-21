package com.example.hw4;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private Context context;
    private static final int TYPE_TOP_NEWS = 0;
    private static final int TYPE_NORMAL_NEWS = 1;

    public NewsAdapter(Context context, List<NewsItem> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP_NEWS;
        } else {
            return TYPE_NORMAL_NEWS;
        }
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_TOP_NEWS) {
            view = LayoutInflater.from(context).inflate(R.layout.top_news, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.news_layout, parent, false);
        }
        return new NewsViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);
        holder.headlineTextView.setText(newsItem.getHeadline());
//        holder.summaryTextView.setText(newsItem.getSummary());
        holder.sourceTextView.setText(newsItem.getSource());
        holder.datetimeTextView.setText(getRelativeTime(Long.parseLong(newsItem.getDatetime()))); // Updated to show relative time


        // Use Picasso to load the image
        if (!newsItem.getImage().isEmpty()) {
            Picasso.get()
                    .load(newsItem.getImage())
                    .resize(100, 100)  // resize the image to 100x100 pixels
                    .centerCrop()
                    .into(holder.imageView);
        }
    }
    private String getRelativeTime(long timestamp) {
        long currentTime = System.currentTimeMillis() / 1000;
        long diffInSeconds = currentTime - timestamp;
        long hours = diffInSeconds / 3600;
        long minutes = (diffInSeconds % 3600) / 60;

        if (hours > 0) {
            return hours + " hours ago";
        } else if (minutes > 0) {
            return minutes + " minutes ago";
        } else {
            return "Just now";
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView headlineTextView;
        private TextView summaryTextView;
        private TextView sourceTextView;
        private TextView datetimeTextView;

        public NewsViewHolder(View itemView, int viewType) {
            super(itemView);
            // Initialize views for both layouts
            if (viewType == TYPE_TOP_NEWS) {
                // Initialize views specific to top_news.xml
                imageView = itemView.findViewById(R.id.top_news_image); // ID from top_news.xml
                headlineTextView = itemView.findViewById(R.id.top_news_title); // ID from top_news.xml
                sourceTextView = itemView.findViewById(R.id.top_news_source);
                datetimeTextView = itemView.findViewById(R.id.top_news_time);
                // ... other views for top_news
            } else {
                // Initialize views for news_layout.xml
                imageView = itemView.findViewById(R.id.news_image);
                headlineTextView = itemView.findViewById(R.id.news_headline);
                sourceTextView = itemView.findViewById(R.id.news_source);
                datetimeTextView = itemView.findViewById(R.id.news_datetime);
            }

            // The click listener can be the same if you want the same behavior for clicks
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    NewsItem newsItem = newsList.get(position);
                    showNewsDetailDialog(newsItem);
                }
            });
        }
        private void showNewsDetailDialog(NewsItem newsItem) {
            // Inflate the custom layout
            View dialogView = LayoutInflater.from(context).inflate(R.layout.news_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialogView);

            // Set the news details to the dialog views
            TextView sourceTextView = dialogView.findViewById(R.id.tv_article_source);
            TextView dateTextView = dialogView.findViewById(R.id.tv_article_date);
            TextView titleTextView = dialogView.findViewById(R.id.tv_article_title);
            TextView descriptionTextView = dialogView.findViewById(R.id.tv_article_description);

            // You might want to convert the timestamp to a human-readable date
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String dateString = formatter.format(new Date(Long.parseLong(newsItem.getDatetime()) * 1000L));

            sourceTextView.setText(newsItem.getSource());
            dateTextView.setText(dateString); // Assuming the datetime is in seconds since epoch
            titleTextView.setText(newsItem.getHeadline());
            descriptionTextView.setText(newsItem.getSummary());

            AlertDialog dialog = builder.create();

            ImageView chromeImageView = dialogView.findViewById(R.id.iv_chrome);
            ImageView twitterImageView = dialogView.findViewById(R.id.iv_twitter);
            ImageView facebookImageView = dialogView.findViewById(R.id.iv_facebook);

            // Example of setting an OnClickListener for the Chrome icon
            chromeImageView.setOnClickListener(view -> {
                // Intent to view article in web browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getUrl()));
                context.startActivity(browserIntent);
            });

            twitterImageView.setOnClickListener(view -> openTwitterShare(newsItem));
            facebookImageView.setOnClickListener(view -> openFacebookShare(newsItem));

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
        private void openTwitterShare(NewsItem newsItem) {
            String tweetText = String.format("%s %s", newsItem.getHeadline(), newsItem.getUrl());
            String tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(tweetText) + "&hashtags=example,demo&via=twitterdev&related=twitterapi,twitter";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
            context.startActivity(intent);
        }

        // Helper method to share on Facebook
        private void openFacebookShare(NewsItem newsItem) {
            String facebookUrl = "https://www.facebook.com/sharer/sharer.php?u=" + Uri.encode(newsItem.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            context.startActivity(intent);
        }

        public void bind(NewsItem newsItem) {
            // Bind the data based on viewType
            int viewType = getItemViewType();
            if (viewType == TYPE_TOP_NEWS) {
                headlineTextView.setText(newsItem.getHeadline());
                sourceTextView.setText(newsItem.getSource());
                datetimeTextView.setText(newsItem.getDatetime());
                // Use Picasso or another image loading library to display the image
                Picasso.get().load(newsItem.getImage()).into(imageView);
            } else {
                // Bind data for normal news layout
                headlineTextView.setText(newsItem.getHeadline());
                sourceTextView.setText(newsItem.getSource());
                datetimeTextView.setText(newsItem.getDatetime());
                // Use Picasso or another image loading library to display the image
                Picasso.get().load(newsItem.getImage()).into(imageView);
            }
        }

    }
}
