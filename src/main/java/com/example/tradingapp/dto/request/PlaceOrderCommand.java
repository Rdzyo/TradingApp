package com.example.tradingapp.dto.request;

import com.example.tradingapp.dto.OrderAssetDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


public record PlaceOrderCommand(@NotNull
                                UUID userId,
                                @Size(max = 300)
                                List<@Valid OrderAssetDto> assets) {}
