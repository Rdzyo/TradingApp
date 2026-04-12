package com.example.tradingapp.port;

import com.example.tradingapp.model.Order;

import java.util.UUID;

public interface OrderPort {

    Order getOrder(UUID orderId);
}
