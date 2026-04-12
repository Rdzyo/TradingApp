package com.example.tradingapp.integration.controller;

import com.example.tradingapp.integration.BaseITTest;
import com.example.tradingapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.UUID;

import static com.example.tradingapp.testutil.UserTestUtil.USER_TABLE;
import static com.example.tradingapp.testutil.UserTestUtil.basicUser;
import static com.example.tradingapp.testutil.UserTestUtil.userWithPortfolio;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerITTest extends BaseITTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Test
    void getUserPortfolioShouldReturnUserPortfolio() throws Exception {
        //given
        var userId = UUID.randomUUID();
        var user = userWithPortfolio(userId);
        var portItem1 = user.getPortfolio().get(0);
        var portItem2 = user.getPortfolio().get(1);
        getUserTable().putItem(userWithPortfolio(userId));


        mockMvc.perform(get("/portfolio/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].symbol").value(portItem1.getSymbol()))
                .andExpect(jsonPath("$.[0].totalQuantity").value(portItem1.getTotalQuantity()))
                .andExpect(jsonPath("$.[0].averagePrice").value(portItem1.getAveragePrice()))
                .andExpect(jsonPath("$.[1].symbol").value(portItem2.getSymbol()))
                .andExpect(jsonPath("$.[1].totalQuantity").value(portItem2.getTotalQuantity()))
                .andExpect(jsonPath("$.[1].averagePrice").value(portItem2.getAveragePrice()));
    }

    @Test
    void getUserPortfolioShouldReturnEmptyResponseIfUserHasNoItemsInPortfolio() throws Exception {
        var userId = UUID.randomUUID();
        getUserTable().putItem(basicUser(userId));

        mockMvc.perform(get("/portfolio/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    void getUserPortfolioShouldThrow404IfUserDoesNotExist() throws Exception {
        var userId = UUID.randomUUID();

        mockMvc.perform(get("/portfolio/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    private DynamoDbTable<User> getUserTable() {
        return dynamoDbEnhancedClient.table(USER_TABLE, TableSchema.fromBean(User.class));
    }
}
