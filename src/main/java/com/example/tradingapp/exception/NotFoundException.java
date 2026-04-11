package com.example.tradingapp.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String symbol) {
        super(String.format("Asset with symbol %s does not exist", symbol));
    }
}
