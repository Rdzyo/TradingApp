package com.example.tradingapp.testutil;

import com.example.tradingapp.dto.OrderAssetDto;
import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.dto.request.PlaceOrderCommand;
import com.example.tradingapp.model.Order;
import com.example.tradingapp.model.OrderAsset;

import java.util.List;
import java.util.UUID;

public class OrderTestUtil {

    public static final String ORDER_TABLE = "Order";

    public static OrderAssetDto orderAssetDto(String symbol, int quantity) {
        return new OrderAssetDto(symbol, quantity);
    }

    public static OrderAsset orderAsset(String symbol, int quantity, Double price) {
        return OrderAsset.builder()
                .symbol(symbol)
                .price(price)
                .quantity(quantity)
                .build();
    }

    public static PlaceOrderCommand placeOrderCommand() {
        return new PlaceOrderCommand(
                UUID.randomUUID(),
                List.of(orderAssetDto("TEST1", 5),
                        orderAssetDto("TEST2", 2)));
    }

    public static Order orderAccepted(UUID orderId, UUID userId) {
        return Order.builder()
                .orderId(orderId)
                .status(OrderStatus.ACCEPTED)
                .userId(userId)
                .assets(List.of(orderAsset("TEST1", 5, 189.50),
                        orderAsset("TEST2", 2, 205.90)))
                .build();
    }

    public static String placeOrderCommandJson(String symbol1, Integer quantity1, String symbol2, Integer quantity2) {
        return String.format("""
                        {
                           "userId": "%s",
                           "assets": [
                             {
                               "symbol": "%s",
                               "quantity": %d
                             },
                             {
                               "symbol": "%s",
                               "quantity": %d
                             }
                           ]
                         }
                """, UUID.randomUUID(), symbol1, quantity1, symbol2, quantity2);
    }
}
