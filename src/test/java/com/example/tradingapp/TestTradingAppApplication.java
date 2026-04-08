package com.example.tradingapp;

import org.springframework.boot.SpringApplication;

public class TestTradingAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(TradingAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
