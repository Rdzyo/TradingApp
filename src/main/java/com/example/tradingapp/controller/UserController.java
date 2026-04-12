package com.example.tradingapp.controller;

import com.example.tradingapp.model.Portfolio;
import com.example.tradingapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/portfolio/{userId}")
    public List<Portfolio> getUserPortfolio(@PathVariable UUID userId) {
        return userService.getUserPortfolio(userId);
    }
}
