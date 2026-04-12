package com.example.tradingapp.dto.request;

import com.example.tradingapp.dto.OrderAssetDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;


public record PlaceOrderCommand(@NotNull(message = "UserId cannot be null")
                                @Schema(description = "Id of the user as UUID", defaultValue = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
                                UUID userId,
                                @Size(min = 1, max = 300, message = "Cannot request less than 1 asset or over 300 assets at once")
                                @Schema(description = "List of the assets to request")
                                List<@Valid OrderAssetDto> assets) {}
