package com.example.tradingapp.controller;

import com.example.tradingapp.dto.request.PlaceOrderCommand;
import com.example.tradingapp.dto.result.OrderResult;
import com.example.tradingapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Tag(name = "Place Order")
    @Operation(summary = "Customer orders some assets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order successfully placed and OrderResult is returned", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "If validation error occurs"),
            @ApiResponse(responseCode = "404", description = "If one of the assets is not found in the database"),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResult placeOrder(@RequestBody @Valid PlaceOrderCommand orderCommand) {
        return orderService.placeOrder(orderCommand);
    }

    //Only for demo purposes
    @Tag(name = "Complete Order")
    @Operation(summary = "Simulate completion of the order and add assets to user's portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order has been completed and assets"),
            @ApiResponse(responseCode = "404", description = "If user is not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    })
    @PutMapping("/order/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void completeOrder(@PathVariable UUID orderId) {
        orderService.completeOrder(orderId);
    }

}
