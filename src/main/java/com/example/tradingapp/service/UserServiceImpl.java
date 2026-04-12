package com.example.tradingapp.service;

import com.example.tradingapp.model.Portfolio;
import com.example.tradingapp.port.PortfolioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PortfolioPort portfolioPort;

    @Override
    public List<Portfolio> getUserPortfolio(UUID userId) {
        return portfolioPort.getUserPortfolio(userId.toString());
    }
}
