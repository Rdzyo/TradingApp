package com.example.tradingapp.repository;

import com.example.tradingapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public Optional<User> getUser(UUID userId) {
        return Optional.ofNullable(getTable().getItem(Key.builder()
                .partitionValue(userId.toString())
                .build()));

    }

    public void updateUserPortfolio(User user) {
        var table = getTable();
        dynamoDbEnhancedClient.transactWriteItems(
                TransactWriteItemsEnhancedRequest.builder()
                        .addPutItem(table, TransactPutItemEnhancedRequest.builder(User.class)
                                .item(user)
                                .build())
                        .build()
        );
    }

    private DynamoDbTable<User> getTable() {
        return dynamoDbEnhancedClient.table("User", TableSchema.fromBean(User.class));
    }
}
