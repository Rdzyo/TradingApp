package com.example.tradingapp.service;

import com.example.tradingapp.dto.OrderAssetDto;
import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.dto.request.PlaceOrderCommand;
import com.example.tradingapp.dto.result.OrderResult;
import com.example.tradingapp.exception.NotFoundException;
import com.example.tradingapp.model.Order;
import com.example.tradingapp.model.OrderAsset;
import com.example.tradingapp.model.Portfolio;
import com.example.tradingapp.model.User;
import com.example.tradingapp.port.AssetPort;
import com.example.tradingapp.port.OrderPort;
import com.example.tradingapp.port.PortfolioPort;
import com.example.tradingapp.repository.OrderRepository;
import com.example.tradingapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.tradingapp.util.CalculationUtil.calculateAveragePrice;
import static com.example.tradingapp.util.CalculationUtil.calculateItemsInOrder;
import static com.example.tradingapp.validation.OrderValidator.validateForDuplicatedAssets;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AssetPort assetPort;
    private final OrderPort orderPort;
    private final PortfolioPort portfolioPort;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    public OrderResult placeOrder(PlaceOrderCommand command) {
        var assets = command.assets();
        validateForDuplicatedAssets(assets);
        var assetsWithPrice = addPriceToAssets(assets);
        var userId = command.userId();
        var order = setOrder(userId, assetsWithPrice);
        orderRepository.createOrder(order);
        var itemsReceived = calculateItemsInOrder(assetsWithPrice);
        return preparePlaceOrderResponse(order.getOrderId(), itemsReceived);
    }

    @Override
    public void completeOrder(UUID orderId) {
        processOrder(orderId);
        orderRepository.updateOrder(orderId, OrderStatus.COMPLETED);
    }

    //Simple process without any validations to reject the order
    //Would suggest doing it in some scheduler depending on use-cases and process orders in bulk
    private void processOrder(UUID orderId) {
        var order = orderPort.getOrder(orderId);
        List<Portfolio> userPortfolio = portfolioPort.getUserPortfolio(order.getUserId().toString());
        if (userPortfolio == null || userPortfolio.isEmpty()) {
            List<Portfolio> portfolioList = new ArrayList<>();
            order.getAssets().forEach(
                    orderAsset -> portfolioList.add(
                            setUpPortfolio(orderAsset.getSymbol(), orderAsset.getQuantity(), orderAsset.getPrice())));
            userRepository.updateUserPortfolio(setUser(order.getUserId(), portfolioList));
        } else {
            //Probably could be written better, would have to spend more time with this lambda
            var portfolioMap = userPortfolio.stream()
                    .collect(Collectors.toMap(Portfolio::getSymbol, portfolio -> portfolio));
            order.getAssets().forEach(orderAsset -> mergePortfolioAsset(portfolioMap, orderAsset));
            userPortfolio = portfolioMap.values().stream().toList();
            userRepository.updateUserPortfolio(setUser(order.getUserId(), userPortfolio));
        }
    }

    private Order setOrder(UUID userId, List<OrderAsset> assets) {
        return Order.builder()
                .orderId(UUID.randomUUID())
                .userId(userId)
                .status(OrderStatus.ACCEPTED)
                .assets(assets)
                .build();
    }

    private User setUser(UUID userId, List<Portfolio> userPortfolio) {
        return User.builder()
                .userId(userId)
                .portfolio(userPortfolio)
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
                                    .orElseThrow(() -> new NotFoundException(
                                            String.format("Asset with symbol %s does not exist", asset.symbol())))
                                    .getPrice())
                            .build()));
        } catch (NotFoundException e) {
            log.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        return assetsWithPrice;
    }

    private void mergePortfolioAsset(Map<String, Portfolio> portfolio, OrderAsset orderAsset) {
        var symbol = orderAsset.getSymbol();
        if (portfolio.containsKey(symbol)) {
            var portfolioAsset = portfolio.get(symbol);
            var totalQuantity = portfolioAsset.getTotalQuantity() + orderAsset.getQuantity();
            var avgPrice = calculateAveragePrice(
                    portfolioAsset.getAveragePrice(),
                    portfolioAsset.getTotalQuantity(),
                    orderAsset.getPrice(),
                    orderAsset.getQuantity());
            var updatedPortfolioAsset = setUpPortfolio(portfolioAsset.getSymbol(), totalQuantity, avgPrice);
            portfolio.replace(symbol, updatedPortfolioAsset);
        } else {
            var newPortfolioAsset = setUpPortfolio(orderAsset.getSymbol(), orderAsset.getQuantity(), orderAsset.getPrice());
            portfolio.put(symbol, newPortfolioAsset);
        }
    }

    private OrderResult preparePlaceOrderResponse(UUID orderId, int itemsReceived) {
        return OrderResult.builder()
                .orderId(orderId)
                .status(OrderStatus.ACCEPTED.name)
                .itemsReceived(itemsReceived)
                .build();
    }

    private Portfolio setUpPortfolio(String symbol, int totalQuantity, Double avgPrice) {
        return Portfolio.builder()
                .symbol(symbol)
                .totalQuantity(totalQuantity)
                .averagePrice(avgPrice)
                .build();
    }
}
