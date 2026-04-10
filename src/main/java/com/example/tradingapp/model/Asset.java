package com.example.tradingapp.model;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@Setter
@DynamoDbBean
public class Asset {
    private String symbol;
    private String name;
    //Double used for simplicity, could be BigDecimal for better precision or even custom class to cover multiple currencies
    private Double price;

    @DynamoDbPartitionKey
    public String getSymbol() {
        return this.symbol;
    }
}
