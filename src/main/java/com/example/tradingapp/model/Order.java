package com.example.tradingapp.model;

import com.example.tradingapp.dto.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Order {
    private UUID orderId;
    private UUID userId;
    private OrderStatus status;
    List<OrderAsset> assets;

    @DynamoDbPartitionKey
    public UUID getOrderId() {
        return this.orderId;
    }
}
