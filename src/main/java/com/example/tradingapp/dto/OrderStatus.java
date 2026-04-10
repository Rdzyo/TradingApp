package com.example.tradingapp.dto;

public enum OrderStatus {
    ACCEPTED("ACCEPTED"),
    COMPLETED("COMPLETED"),
    REJECTED("REJECTED");

    public final String name;

    OrderStatus(String name) {
        this.name = name;
    }
}
