package com.example.tradingapp.repository;

import com.example.tradingapp.dto.OrderStatus;
import com.example.tradingapp.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryImpl implements OrderRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final String TABLE_NAME = "Order";

    @Override
    public void createOrder(Order order) {
        try {
            var table = getTable();
            dynamoDbEnhancedClient.transactWriteItems(b -> b
                    .addPutItem(table, TransactPutItemEnhancedRequest
                            .builder(Order.class)
                            .item(order)
                            //Should be extremely low chance for that, since UUID is used, but better be safe
                            .conditionExpression(Expression.builder().expression("attribute_not_exists (orderId)").build())
                            .build()));
        } catch (ConditionalCheckFailedException e) {
            log.info("Order with orderId {} already exists", order.getOrderId());
        }
        log.info("Order {} successfully saved", order.getOrderId());
    }

    @Override
    public void updateOrder(UUID orderId,  OrderStatus status) {
        Map<String, AttributeValue> key = Map.of("orderId", AttributeValue.fromS(orderId.toString()));
        Map<String, AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put("status", AttributeValueUpdate.builder()
                        .value(AttributeValue.fromS(status.name))
                        .action(AttributeAction.PUT)
                .build());
        //Using standard client instead of enhanced client (would require 2 calls to db, getting an item and then updating)
        dynamoDbEnhancedClient.dynamoDbClient()
                .updateItem(UpdateItemRequest.builder()
                        .tableName(TABLE_NAME)
                        .key(key)
                        .attributeUpdates(updatedValues)
                        .returnValues(ReturnValue.ALL_NEW)
                        .build());
        log.info("Order with id {} has been updated to status {}", orderId, status);
    }

    @Override
    public Order getOrder(UUID orderId) {
        return getTable().getItem(Key.builder()
                .partitionValue(orderId.toString())
                .build());
    }

    private DynamoDbTable<Order> getTable() {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Order.class));
    }
}
