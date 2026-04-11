package com.example.tradingapp.validation;

import com.example.tradingapp.dto.OrderAssetDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

public class OrderValidator {

    public static void validateForDuplicatedAssets(List<OrderAssetDto> assets) {
        var hasDuplicates = assets.stream()
                .map(OrderAssetDto::symbol)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
        if(hasDuplicates) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Found duplicated assets in the order, please verify your request and remove any duplicates");
        }
    }
}
