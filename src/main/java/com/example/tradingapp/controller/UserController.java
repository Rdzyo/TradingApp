package com.example.tradingapp.controller;

import com.example.tradingapp.model.Portfolio;
import com.example.tradingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Tag(name = "Retrieve portfolio")
    @Operation(summary = "Retrieve user's portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's portfolio"),
            @ApiResponse(responseCode = "404", description = "User does not exist")
    })
    @GetMapping("/portfolio/{userId}")
    public List<Portfolio> getUserPortfolio(@PathVariable UUID userId) {
        return userService.getUserPortfolio(userId);
    }
}
