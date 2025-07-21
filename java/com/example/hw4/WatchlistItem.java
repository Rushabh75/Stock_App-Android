package com.example.hw4;


public class WatchlistItem {
    private String userId;
    private String symbol;
    private String name;
    private double currentPrice;
    private double change;
    private double changePercent;

    // Constructor
    public WatchlistItem(String userId, String symbol, String name, double currentPrice, double change, double changePercent) {
        this.userId = userId;
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.change = change;
        this.changePercent = changePercent;
    }

    // Getters and setters for each field
    public String getUserId() { return "rushabh75"; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public double getChange() { return change; }
    public void setChange(double change) { this.change = change; }
    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }
}
