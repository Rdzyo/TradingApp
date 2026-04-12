package com.example.tradingapp.unit.service;

import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.port.AssetPort;
import com.example.tradingapp.port.OrderPort;
import com.example.tradingapp.port.PortfolioPort;
import com.example.tradingapp.repository.OrderRepository;
import com.example.tradingapp.repository.UserRepository;
import com.example.tradingapp.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.tradingapp.testutil.AssetTestUtil.asset;
import static com.example.tradingapp.testutil.OrderTestUtil.orderAccepted;
import static com.example.tradingapp.testutil.OrderTestUtil.placeOrderCommand;
import static com.example.tradingapp.testutil.UserTestUtil.portfolio;
import static com.example.tradingapp.testutil.UserTestUtil.userWithPortfolio;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    AssetPort assetPort;
    @Mock
    OrderPort orderPort;
    @Mock
    PortfolioPort portfolioPort;
    @Mock
    OrderRepository orderRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void placeOrderShouldCreateOrderSuccessfully() {
        //given
        var placeOrderCommand = placeOrderCommand();
        var firstAsset = placeOrderCommand.assets().get(0);
        var secondAsset = placeOrderCommand.assets().get(1);
        var symbol1 = firstAsset.symbol();
        var symbol2 = secondAsset.symbol();
        var itemsReceived = firstAsset.quantity() + secondAsset.quantity();
        when(assetPort.getAsset(symbol1)).thenReturn(Optional.of(asset(symbol1, "TEST_NAME_1")));
        when(assetPort.getAsset(symbol2)).thenReturn(Optional.of(asset(symbol2, "TEST_NAME_2")));

        //when
        var response = orderService.placeOrder(placeOrderCommand);

        //then
        assertEquals(OrderStatus.ACCEPTED.name, response.getStatus());
        assertEquals(itemsReceived, response.getItemsReceived());
    }

    @Test
    void placeOrderShouldThrowNotFoundExceptionIfAssetNotExist() {
        //given
        var placeOrderCommand = placeOrderCommand();
        var firstAsset = placeOrderCommand.assets().getFirst();
        var errorMessage = String.format("Asset with symbol %s does not exist", firstAsset.symbol());
        when(assetPort.getAsset(firstAsset.symbol())).thenReturn(Optional.empty());

        //then
        var exception = assertThrows(ResponseStatusException.class, () -> orderService.placeOrder(placeOrderCommand));
        assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    void completeOrderShouldAddItemsToUserPortfolioAndChangeStatusToCompleted() {
        //given
        var orderId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        when(orderPort.getOrder(orderId)).thenReturn(orderAccepted(orderId, userId));
        when(portfolioPort.getUserPortfolio(anyString())).thenReturn(null);

        //when
        orderService.completeOrder(orderId);

        //then
        verify(userRepository, times(1)).updateUserPortfolio(any());
        verify(orderRepository, times(1)).updateOrder(any(), any());
    }

    @Test
    void completeOrderShouldMergeItemsToExistingUserPortfolioAndChangeStatusToCompleted() {
        //given
        var orderId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var user = userWithPortfolio(userId);
        when(orderPort.getOrder(orderId)).thenReturn(orderAccepted(orderId, userId));
        when(portfolioPort.getUserPortfolio(anyString())).thenReturn(user.getPortfolio());

        //when
        orderService.completeOrder(orderId);

        //then
        verify(userRepository, times(1)).updateUserPortfolio(any());
        verify(orderRepository, times(1)).updateOrder(any(), any());
    }

    @Test
    void completeOrderShouldAddNewItemsToExistingUserPortfolioAndChangeStatusToCompleted() {
        //given
        var orderId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        when(orderPort.getOrder(orderId)).thenReturn(orderAccepted(orderId, userId));
        when(portfolioPort.getUserPortfolio(anyString())).thenReturn(List.of(portfolio("newSymbol", 10, 580.20)));

        //when
        orderService.completeOrder(orderId);

        //then
        verify(userRepository, times(1)).updateUserPortfolio(any());
        verify(orderRepository, times(1)).updateOrder(any(), any());
    }
}
