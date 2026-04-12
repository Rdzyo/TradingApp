package com.example.tradingapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderAssetDto(@NotNull(message = "Symbol cannot be null")
                            @NotBlank(message = "Symbol cannot be blank")
                            @Schema(description = "Symbol of the asset", defaultValue = "AAPL")
                            String symbol,
                            @Min(value = 1, message = "Quantity has to be at least 1")
                            @Schema(description = "Number of items to request", defaultValue = "1")
                            int quantity) {}
