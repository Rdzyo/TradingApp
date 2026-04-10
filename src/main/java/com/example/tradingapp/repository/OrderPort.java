package com.example.tradingapp.repository;

import com.example.tradingapp.model.Order;

import java.util.UUID;

public interface OrderPort {

    void createOrder(Order order);

    void completeOrder(UUID orderId);
}
