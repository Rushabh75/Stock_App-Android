package com.example.hw4;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.fragment.app.Fragment;

public class RecommendationChartFragment extends Fragment {

    private WebView recommendationWebView;

    public static RecommendationChartFragment newInstance(String recommendationData) {
        RecommendationChartFragment fragment = new RecommendationChartFragment();
        Bundle args = new Bundle();
        args.putString("recommendationData", recommendationData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommendation_layout, container, false);
        recommendationWebView = view.findViewById(R.id.webViewRecommendationTrends);
        initializeWebView();
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView() {
        recommendationWebView.getSettings().setJavaScriptEnabled(true);
        recommendationWebView.getSettings().setDomStorageEnabled(true);
        recommendationWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (getArguments() != null) {
                    String recommenddata = getArguments().getString("recommendationData");
                    view.evaluateJavascript("ChartRecommend(" + recommenddata + ");", null);
                }
            }
        });

        recommendationWebView.loadUrl("file:///android_asset/recommendation.html");
    }

}

