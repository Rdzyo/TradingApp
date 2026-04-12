package com.example.tradingapp.util;

import com.example.tradingapp.model.OrderAsset;

import java.util.List;

public class CalculationUtil {

    public static Double calculateAveragePrice(Double portfolioAvgPrice, int portfolioQuantity, Double orderPrice, int orderQuantity) {
        return ((portfolioAvgPrice * portfolioQuantity) + (orderPrice * orderQuantity)) / (portfolioQuantity + orderQuantity);
    }

    public static int calculateItemsInOrder(List<OrderAsset> assets) {
        return assets.stream().map(OrderAsset::getQuantity).reduce(Integer::sum).get();
    }
}
