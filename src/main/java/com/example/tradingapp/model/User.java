package com.example.tradingapp.model;


import com.example.tradingapp.dto.PortfolioDto;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@DynamoDbBean
public class User {
    private UUID userId;
    private List<PortfolioDto> portfolio;

    @DynamoDbPartitionKey
    public UUID getUserId() {
        return this.userId;
    }
}
