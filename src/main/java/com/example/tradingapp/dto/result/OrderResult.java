package com.example.tradingapp.dto.result;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderResult {
    private UUID orderId;
    private String status;
    private int itemsReceived;
}
