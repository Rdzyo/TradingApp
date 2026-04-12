package com.example.tradingapp.port;

import com.example.tradingapp.model.Order;
import com.example.tradingapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderPortImpl implements OrderPort {

    private final OrderRepository orderRepository;

    @Override
    public Order getOrder(UUID orderId) {
        return orderRepository.getOrder(orderId);
    }
}
