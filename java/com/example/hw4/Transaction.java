package com.example.hw4;

public class Transaction {
    private String userId;
    private String symbol;
    private int quantity;
    private double price;

    public Transaction(String userId, String symbol, int quantity, double price) {
        this.userId = userId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
}
