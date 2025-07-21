package com.example.hw4;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.fragment.app.Fragment;

public class ChartFragment extends Fragment {

    private static final String ARG_CHART_TYPE = "chart_type";
    private WebView chartWebView;
    private String symbol;

    // Factory method to create a new instance of the fragment
    public static ChartFragment newInstance(String chartType, String symbol) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHART_TYPE, chartType);
        args.putString("SYMBOL", symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        chartWebView = view.findViewById(R.id.chartWebView);
        initializeWebView();
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView() {
        chartWebView.getSettings().setJavaScriptEnabled(true);
        chartWebView.getSettings().setDomStorageEnabled(true);
        chartWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Once the page is loaded, we can load the chart data
                loadChartData(); // Let's define a method to load data
            }
        });
        loadChart(getArguments().getString(ARG_CHART_TYPE), getArguments().getString("SYMBOL"));
    }

    public void loadChart(String chartType, String symbol) {
        this.symbol = symbol; // Store the symbol
        String assetFileName = chartType.equals("Hourly") ? "hourly.html" : "historical.html";
        chartWebView.loadUrl("file:///android_asset/" + assetFileName);
    }

    private void loadChartData() {
        // Fetch and prepare your data here, then update the chart
        String data = "{}"; // Dummy data
        updateChartWithData(data, getArguments().getString(ARG_CHART_TYPE), symbol);
    }

    public void updateChartWithData(String data, String chartType, String symbol) {
        if (chartWebView != null) {
            String javascriptCode = chartType.equals("Hourly") ?
                    "javascript:plotHourlyChart(" + data + ", '" + symbol + "');" :
                    "javascript:plotChart(" + data + ", '" + symbol + "');";

            chartWebView.evaluateJavascript(javascriptCode, null);
        }
    }
}

