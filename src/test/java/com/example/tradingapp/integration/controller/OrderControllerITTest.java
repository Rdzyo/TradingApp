package com.example.tradingapp.integration.controller;

import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.integration.BaseITTest;
import com.example.tradingapp.model.Asset;
import com.example.tradingapp.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.UUID;

import static com.example.tradingapp.testutil.AssetTestUtil.ASSET_TABLE;
import static com.example.tradingapp.testutil.AssetTestUtil.asset;
import static com.example.tradingapp.testutil.OrderTestUtil.ORDER_TABLE;
import static com.example.tradingapp.testutil.OrderTestUtil.orderAccepted;
import static com.example.tradingapp.testutil.OrderTestUtil.placeOrderCommandJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerITTest extends BaseITTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    private static final Asset ASSET1 = asset("TEST1", "TEST_NAME1");
    private static final Asset ASSET2 = asset("TEST2", "TEST_NAME2");

    @Test
    void placeOrderShouldSuccessAndReturnOrderStatusAccepted() throws Exception {
        //given
        var table = getAssetTable();
        table.putItem(ASSET1);
        table.putItem(ASSET2);
        var placeOrderJson = placeOrderCommandJson(ASSET1.getSymbol(), 1, ASSET2.getSymbol(), 1);

        //expect
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placeOrderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED.name));

        //tearDown
        table.deleteItem(ASSET1);
        table.deleteItem(ASSET2);
    }

    @Test
    void placeOrderShouldReturn404WhenAssetNotFound() throws Exception {
        var placeOrderJson = placeOrderCommandJson(ASSET1.getSymbol(), 1, ASSET2.getSymbol(), 1);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placeOrderJson))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "' ', 1",
            "'', 1",
            "TEST, 0",
            "TEST, NIL"
    }, nullValues = "NIL")
    void placeOrderShouldReturn400WhenValidationErrorOccurs(String symbol, Integer quantity) throws Exception {
        var placeOrderJson = placeOrderCommandJson(symbol, quantity, ASSET2.getSymbol(), 1);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placeOrderJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completeOrderShouldSuccessfullyUpdateOrderToCompletedStatus() throws Exception {
        var table = getOrderTable();
        var orderId = UUID.randomUUID();
        table.putItem(orderAccepted(orderId));

        mockMvc.perform(put("/order/{orderId}", orderId))
                .andExpect(status().isNoContent());

        var completedOrder = table.getItem(Key.builder()
                                        .partitionValue(AttributeValue.fromS(orderId.toString()))
                                        .build());

        assertEquals(OrderStatus.COMPLETED, completedOrder.getStatus());
    }

    private DynamoDbTable<Asset> getAssetTable() {
        return dynamoDbEnhancedClient.table(ASSET_TABLE, TableSchema.fromBean(Asset.class));
    }

    private DynamoDbTable<Order> getOrderTable() {
        return dynamoDbEnhancedClient.table(ORDER_TABLE, TableSchema.fromBean(Order.class));
    }
}
