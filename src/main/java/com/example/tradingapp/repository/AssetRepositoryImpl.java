package com.example.tradingapp.repository;


import com.example.tradingapp.model.Asset;
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
public class AssetRepositoryImpl implements AssetRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    //Could be cached to not overload the db too much with scan (maybe scheduled scan if a lot of assets?)
    //Improvement: return it as page not list
    @Override
    public List<Asset> getAvailableAssets() {
        return getTable().scan().items().stream().toList();
    }

    @Override
    public Optional<Asset> getAsset(String symbol) {
        return Optional.ofNullable(getTable().getItem(Key.builder()
                .partitionValue(symbol)
                .build()));
    }

    private DynamoDbTable<Asset> getTable() {
        return dynamoDbEnhancedClient.table("Asset", TableSchema.fromBean(Asset.class));
    }
}
