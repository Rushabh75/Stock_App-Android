package com.example.hw4;

public class PortfolioItem {
    private String userId;
    private String symbol;
    private double avgPrice;
    private int quantity;
    private double totalCost;
    private double change;
    private double changePercent;
    private double purchasePrice;
    private double sellPrice;
    private double current;
    private double finalcost;



    public PortfolioItem(String userId, String symbol, int quantity,double purchasePrice, double sellPrice) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.purchasePrice = purchasePrice;
        this.sellPrice = sellPrice;

    }
    public double getChange() {
        return change;
    }

    public void setCurrent(double current) {
        this.current = current;
        this.change = current - (totalCost/quantity);
    }

    public double getChangePercent() {
        changePercent = ((current-(totalCost/quantity))/(totalCost/quantity))*100;
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public double getAvgPrice() { return avgPrice; }
    public void setAvgPrice(double avgPrice) { this.avgPrice = avgPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalCost() {return totalCost; }
    public double getFinalcost() {
        finalcost = current*quantity;
        return finalcost;
    }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double sellPrice) { this.sellPrice = sellPrice; }
    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }
}