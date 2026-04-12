package com.example.tradingapp.repository;

import com.example.tradingapp.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> getUser(UUID userId);
    void updateUserPortfolio(User user);
}
