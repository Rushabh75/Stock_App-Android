package com.example.hw4;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Search {
    private static final String TAG = "StockAPI";
    private final OkHttpClient client = new OkHttpClient();
    private final String baseUrl = "https://backend3-419023.wl.r.appspot.com/api";
    private Map<String, String> apiResponses = new HashMap<>();
    private Context context;

    public Search(Context context) {
        this.context = context;
    }
    public interface OnDataFetchedListener {
        void onDataFetched(Map<String, String> apiResponses);
    }

    private OnDataFetchedListener onDataFetchedListener;

    // Constructor that takes the callback listener
    public Search(Context context, OnDataFetchedListener onDataFetchedListener) {
        this.context = context;
        this.onDataFetchedListener = onDataFetchedListener;
    }

    private void notifyDataFetched() {
        if (onDataFetchedListener != null) {
            onDataFetchedListener.onDataFetched(apiResponses);
        }
    }

    public void fetchStockData(String symbol) {
        fetchStockProfile(symbol);
        fetchStockQuote(symbol);
        fetchStockPeers(symbol);
        fetchCompanyNews(symbol);
        fetchInsiderSentiment(symbol);
        fetchRecommendations(symbol);
        fetchEarnings(symbol);
        fetchhistorical(symbol);
        fetchhourly(symbol);
        fetchsentiments(symbol);
    }
    private void fetchhistorical(String ticker) {
        String url = baseUrl + "/mainchart/" + ticker;
        makeApiRequest(url, "Historical");
    }

    private void fetchhourly(String ticker) {
        // Get current date and time in UTC
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));

        // Get the start of the previous day (midnight)
        ZonedDateTime startOfPreviousDay = now.toLocalDate().minusDays(1).atStartOfDay(ZoneId.of("UTC"));

        // Convert to Unix timestamp (seconds since epoch)
        long quotetime = startOfPreviousDay.toEpochSecond();

        // Construct the URL with the timestamp
        String url = baseUrl + "/historical/" + ticker + "/" + quotetime;

        // Make the API request
        makeApiRequest(url, "Hourly");
    }

    private void fetchsentiments(String ticker) {
        String url = baseUrl + "/stock/insider-sentiment?ticker=" + ticker;
        makeApiRequest(url, "Sentiments");
    }
    private void fetchStockProfile(String ticker) {
        String url = baseUrl + "/stock-profile?ticker=" + ticker;
        makeApiRequest(url, "StockProfile");
    }

    public void fetchStockQuote(String symbol) {
        String url = baseUrl + "/stock-quote?symbol=" + symbol;
        makeApiRequest(url, "StockQuote");
    }

    private void fetchStockPeers(String ticker) {
        String url = baseUrl + "/stock-peers?ticker=" + ticker;
        makeApiRequest(url, "StockPeers");
    }

    private void fetchCompanyNews(String ticker) {
        String url = baseUrl + "/filtered-company-news/" + ticker;
        makeApiRequest(url, "CompanyNews");
    }

    private void fetchInsiderSentiment(String ticker) {
        String url = baseUrl + "/stock/insider-sentiment?ticker=" + ticker;
        makeApiRequest(url, "InsiderSentiment");
    }

    private void fetchRecommendations(String ticker) {
        String url = baseUrl + "/stock/recommendation/" + ticker;
        makeApiRequest(url, "Recommendation");
    }

    private void fetchEarnings(String ticker) {
        String url = baseUrl + "/stock/earnings/" + ticker;
        makeApiRequest(url, "StockEarnings");
    }

    private void makeApiRequest(String url, final String apiName) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, apiName + " Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, apiName + " Response: " + responseBody);
                    synchronized (apiResponses) {
                        apiResponses.put(apiName, responseBody);
                        notifyDataFetched();
                    }

                } else {
                    Log.e(TAG, apiName + " Error: " + response.message());
                }
            }
        });
    }

    // Synchronized access to the stored API responses to handle concurrent updates and reads
    public synchronized String getApiResponse(String apiName) {
        return apiResponses.get(apiName);
    }
}
