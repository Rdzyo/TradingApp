package com.example.tradingapp.service;

import com.example.tradingapp.dto.OrderAssetDto;
import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.dto.request.PlaceOrderCommand;
import com.example.tradingapp.dto.result.OrderResult;
import com.example.tradingapp.exception.NotFoundException;
import com.example.tradingapp.model.Order;
import com.example.tradingapp.model.OrderAsset;
import com.example.tradingapp.repository.AssetPort;
import com.example.tradingapp.repository.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.tradingapp.validation.OrderValidator.validateForDuplicatedAssets;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AssetPort assetPort;
    private final OrderPort orderPort;

    @Override
    public OrderResult placeOrder(PlaceOrderCommand command) {
        var assets = command.assets();
        validateForDuplicatedAssets(assets);
        var assetsWithPrice = addPriceToAssets(assets);
        var userId = command.userId();
        var order = setOrder(userId, assetsWithPrice);
        orderPort.createOrder(order);
        var itemsReceived = calculateItemsInOrder(assetsWithPrice);
        return preparePlaceOrderResponse(order.getOrderId(), itemsReceived);
    }

    @Override
    public void completeOrder(UUID orderId) {
        orderPort.completeOrder(orderId);
    }

    private Order setOrder(UUID userId, List<OrderAsset> assets) {
        return Order.builder()
                .orderId(UUID.randomUUID())
        .userId(userId)
                .status(OrderStatus.ACCEPTED)
                .assets(assets)
                .build();
    }

    private List<OrderAsset> addPriceToAssets(List<OrderAssetDto> assets) {
        List<OrderAsset> assetsWithPrice = new ArrayList<>();
        try {
            //Could be improved instead of using getAsset, all symbols could be aggregated and create batchGetItem call.
            //Instead of max of 300 calls, it would call db max 3 times (batch handles 100 items per call) and throw NotFound if lists would have different sizes.
            //Price cache would be a good idea too, depends on how often price is changing.
            assets.forEach(asset ->
                    assetsWithPrice.add(OrderAsset.builder()
                            .symbol(asset.symbol())
                            .quantity(asset.quantity())
                            .price(assetPort.getAsset(asset.symbol())
                                    .orElseThrow(() -> new NotFoundException(asset.symbol())).getPrice())
                            .build()));
        } catch (NotFoundException e) {
            log.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return assetsWithPrice;
    }

    private int calculateItemsInOrder(List<OrderAsset> assets) {
        return assets.stream().map(OrderAsset::getQuantity).reduce(Integer::sum).get();
    }

    private OrderResult preparePlaceOrderResponse(UUID orderId, int itemsReceived) {
        return OrderResult.builder()
                .orderId(orderId)
                .status(OrderStatus.ACCEPTED.name)
                .itemsReceived(itemsReceived)
                .build();
    }
}
