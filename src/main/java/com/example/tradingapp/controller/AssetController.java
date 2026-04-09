package com.example.tradingapp.controller;

import com.example.tradingapp.entity.Asset;
import com.example.tradingapp.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/assets")
    public List<Asset> getAssets() {
        return assetService.getAvailableAssets();
    }

}
