package com.example.tradingapp.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.UUID;

@Getter
@Setter
@DynamoDbBean
public class Order {
    private UUID orderId;
    //Could be enum
    private String status;
    private int itemsReceived;

    @DynamoDbPartitionKey
    public UUID getOrderId() {
        return this.orderId;
    }

    @DynamoDbSortKey
    public String status() {
        return this.status;
    }
}
