package com.example.tradingapp.integration.controller;

import com.example.tradingapp.integration.BaseITTest;
import com.example.tradingapp.model.Asset;
import com.example.tradingapp.scheduler.AssetPriceScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Duration;
import java.util.List;

import static com.example.tradingapp.testutil.AssetTestUtil.ASSET_TABLE;
import static com.example.tradingapp.testutil.AssetTestUtil.asset;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AssetControllerITTest extends BaseITTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoSpyBean
    AssetPriceScheduler assetPriceScheduler;

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
    void getAssetsShouldReturnListOfAssetsWithUpdatedPrices() throws Exception {
        //given
        var table = dynamoDbEnhancedClient.table(ASSET_TABLE, TableSchema.fromBean(Asset.class));
        var asset1 = asset("TEST1", "TEST_NAME1");
        var asset2 = asset("TEST2", "TEST_NAME2");
        table.putItem(asset1);
        table.putItem(asset2);
        await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> verify(assetPriceScheduler, times(1)).changeAssetPriceTask());
        //Not ideal, but the scheduler's task needs a second to update values for successful test
        Thread.sleep(Duration.ofSeconds(1));

        //then
        mockMvc.perform(get("/assets"))
                .andExpect(status().isOk())
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString();
                    String symbol1 = JsonPath.parse(json).read("$.[0].symbol").toString();
                    Double updatedPrice1 = Double.valueOf(JsonPath.parse(json).read("$.[0].price").toString());
                    assertEquals(symbol1, asset1.getSymbol());
                    assertNotEquals(updatedPrice1, asset1.getPrice());
                    String symbol2 = JsonPath.parse(json).read("$.[1].symbol").toString();
                    Double updatedPrice2 = Double.valueOf(JsonPath.parse(json).read("$.[1].price").toString());
                    assertEquals(symbol2, asset2.getSymbol());
                    assertNotEquals(updatedPrice2, asset2.getPrice());
                });
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
