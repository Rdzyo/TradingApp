package com.example.tradingapp.port;

import com.example.tradingapp.model.Portfolio;

import java.util.List;

public interface PortfolioPort {
    List<Portfolio> getUserPortfolio(String userId);
}
