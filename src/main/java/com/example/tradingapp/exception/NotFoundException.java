package com.example.tradingapp.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(String.format(message));
    }
}
