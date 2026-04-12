package com.example.tradingapp.repository;

import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.model.Order;

import java.util.UUID;

public interface OrderRepository {
    void createOrder(Order order);
    void updateOrder(UUID orderId, OrderStatus status);
    Order getOrder(UUID orderId);
}
