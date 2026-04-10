package com.example.tradingapp.service;

import com.example.tradingapp.dto.request.PlaceOrderCommand;
import com.example.tradingapp.dto.result.OrderResult;

import java.util.UUID;

public interface OrderService {
    OrderResult placeOrder(PlaceOrderCommand command);
    void completeOrder(UUID orderId);
}
