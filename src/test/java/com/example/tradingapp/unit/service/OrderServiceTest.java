package com.example.tradingapp.unit.service;

import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.repository.AssetPort;
import com.example.tradingapp.repository.OrderPort;
import com.example.tradingapp.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.example.tradingapp.testutil.AssetTestUtil.asset;
import static com.example.tradingapp.testutil.OrderTestUtil.placeOrderCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    AssetPort assetPort;
    @Mock
    OrderPort orderPort;
    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderSuccessfully() {
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
    void notFoundExceptionShouldBeThrownIfAssetNotExists() {
        //given
        var placeOrderCommand = placeOrderCommand();
        var firstAsset = placeOrderCommand.assets().getFirst();
        var errorMessage = String.format("Asset with symbol %s does not exist", firstAsset.symbol());
        when(assetPort.getAsset(firstAsset.symbol())).thenReturn(Optional.empty());

        //then
        var exception = assertThrows(ResponseStatusException.class, () -> orderService.placeOrder(placeOrderCommand));
        assertTrue(exception.getMessage().contains(errorMessage));
    }
}
