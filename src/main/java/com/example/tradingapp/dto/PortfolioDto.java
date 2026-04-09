package com.example.tradingapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PortfolioDto {
    private String symbol;
    private int totalQuantity;
    private Double averagePrice;
}
