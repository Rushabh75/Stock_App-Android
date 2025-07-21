package com.example.hw4;

import com.google.gson.annotations.SerializedName;

public class StockResponse {
    private GlobalQuote globalQuote;

    public static class GlobalQuote {
        @SerializedName("05. price")
        private double price;

        @SerializedName("09. change")
        private double change;

        @SerializedName("10. change percent")
        private double changePercent;

        public double getPrice() {
            return price;
        }

        public double getChange() {
            return change;
        }

        public double getChangePercent() {
            return changePercent;
        }
    }

    public GlobalQuote getGlobalQuote() {
        return globalQuote;
    }
}

