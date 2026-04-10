package com.example.tradingapp.unit.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.tradingapp.testutil.OrderTestUtil.orderAssetDto;
import static com.example.tradingapp.validation.OrderValidator.validateForDuplicatedAssets;

public class OrderValidatorTest {

    @Test
    void shouldThrowExceptionWhenDuplicatesAreFound() {
        //given
        var duplicateSymbol = "TEST_SYMBOL";
        var orderAssets = List.of(
                orderAssetDto(duplicateSymbol, 5),
                orderAssetDto(duplicateSymbol, 2));

        //then
        Assertions.assertThrows(ResponseStatusException.class, () -> validateForDuplicatedAssets(orderAssets));
    }

    @Test
    void shouldNotThrowExceptionWhenThereAreNoDuplicates() {
        //given
        var symbol1 = "TEST_SYMBOL1";
        var symbol2 = "TEST_SYMBOL2";
        var orderAssets = List.of(
                orderAssetDto(symbol1, 2),
                orderAssetDto(symbol2, 5));

        //then
        Assertions.assertDoesNotThrow(() -> validateForDuplicatedAssets(orderAssets));
    }
}
