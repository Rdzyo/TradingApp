package com.example.tradingapp.testutil;

import com.example.tradingapp.model.Portfolio;
import com.example.tradingapp.model.User;

import java.util.List;
import java.util.UUID;

public class UserTestUtil {

    public static final String USER_TABLE = "User";

    public static User basicUser(UUID uuid) {
        return User.builder()
                .userId(uuid)
                .build();
    }

    public static User userWithPortfolio(UUID uuid) {
        return User.builder()
                .userId(uuid)
                .portfolio(List.of(portfolio("TEST1", 10, 180.29),
                        portfolio("TEST2", 15, 205.58)))
                .build();
    }

    public static Portfolio portfolio(String symbol, int quantity, Double avgPrice) {
        return Portfolio.builder()
                .symbol(symbol)
                .totalQuantity(quantity)
                .averagePrice(avgPrice)
                .build();
    }
}
