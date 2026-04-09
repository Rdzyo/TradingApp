package com.example.tradingapp.repository;


import com.example.tradingapp.entity.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AssetRepositoryImpl implements AssetPort {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Override
    public List<Asset> getAvailableAssets() {
        return getTable().scan().items().stream().toList();
    }

    @Override
    public Optional<Asset> getAsset(String symbol) {
        return Optional.of(getTable().getItem(Key.builder()
                .partitionValue(symbol)
                .build()));
    }

    private DynamoDbTable<Asset> getTable() {
        return dynamoDbEnhancedClient.table("Asset", TableSchema.fromBean(Asset.class));
    }
}
