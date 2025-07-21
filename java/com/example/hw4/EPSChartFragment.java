package com.example.hw4;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

public class EPSChartFragment extends Fragment {

    private WebView webView;

    public static EPSChartFragment newInstance(String epsData) {
        EPSChartFragment fragment = new EPSChartFragment();
        Bundle args = new Bundle();
        args.putString("epsData", epsData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.eps_layout, container, false);
        webView = view.findViewById(R.id.epsWebView);
        initializeWebView();
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (getArguments() != null) {
                    String epsData = getArguments().getString("epsData");
                    view.evaluateJavascript("ChartEarn(" + epsData + ");", null);
                }
            }
        });
        webView.loadUrl("file:///android_asset/EPS.html");
    }
}
