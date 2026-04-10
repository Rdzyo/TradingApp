package com.example.tradingapp.controller;

import com.example.tradingapp.dto.request.PlaceOrderCommand;
import com.example.tradingapp.dto.result.OrderResult;
import com.example.tradingapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResult placeOrder(@RequestBody @Valid PlaceOrderCommand orderCommand) {
        return orderService.placeOrder(orderCommand);
    }

    //Only for demo purposes, completing order probably would be much more complicated process (could be done in scheduled operation)
    @PutMapping("/order/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeOrder(@PathVariable UUID orderId) {
        orderService.completeOrder(orderId);
    }

}
