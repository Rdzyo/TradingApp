package com.example.tradingapp.integration.controller;

import com.example.tradingapp.integration.BaseITTest;
import com.example.tradingapp.model.Asset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

import static com.example.tradingapp.testutil.AssetTestUtil.ASSET_TABLE;
import static com.example.tradingapp.testutil.AssetTestUtil.asset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AssetControllerITTest extends BaseITTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @BeforeEach
    void setUp() {
       var table = dynamoDbEnhancedClient.table(ASSET_TABLE, TableSchema.fromBean(Asset.class));
       table.scan().items().forEach(table::deleteItem);
    }

    @Test
    void getAssetsShouldReturnListOfAssets() throws Exception {
        //given
        var table = dynamoDbEnhancedClient.table(ASSET_TABLE, TableSchema.fromBean(Asset.class));
        var asset1 = asset("TEST1", "TEST_NAME1");
        var asset2 = asset("TEST2", "TEST_NAME2");
        var expectedResponse = List.of(asset1, asset2);
        table.putItem(asset1);
        table.putItem(asset2);

        //then
        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(content().json(convertObjectToJsonString(expectedResponse)));
    }

    @Test
    void getAssetsShouldReturnEmptyList() throws Exception {
        //expect
        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

    }

    private String convertObjectToJsonString(List<Asset> assets) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(assets);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
