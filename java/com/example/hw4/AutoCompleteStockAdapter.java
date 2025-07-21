package com.example.hw4;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutoCompleteStockAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> resultList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();

    public AutoCompleteStockAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    findStocks(constraint.toString());
                    // We do not set filterResults.values here because it's handled asynchronously
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
    }

    private void findStocks(String searchTerm) {
        String url = "http://10.0.2.2:3000/api/search?q=" + searchTerm; // Ensure this URL is correct and accessible
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    Log.d("API Response", jsonData);
                    try {
//                        String jsonData = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonData);
                        resultList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject stock = jsonArray.getJSONObject(i);
                            resultList.add(stock.getString("symbol") + " - " + stock.getString("description"));
                        }
                        // As we are updating the UI component from background thread, we need to post this on the UI thread
                        ((MainActivity) getContext()).runOnUiThread(() -> {
                            notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        response.body().close();
                    }
                }
            }
        });
    }
}

