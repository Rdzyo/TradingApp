package com.example.tradingapp.service;

import com.example.tradingapp.model.Portfolio;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<Portfolio> getUserPortfolio(UUID userId);
}
