package com.example.tradingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderAssetDto(@NotNull
                            @NotBlank
                            String symbol,
                            @Min(1)
                            int quantity) {}
