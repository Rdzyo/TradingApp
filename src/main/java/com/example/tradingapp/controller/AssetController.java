package com.example.tradingapp.controller;

import com.example.tradingapp.model.Asset;
import com.example.tradingapp.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @Tag(name = "Retrieve all assets")
    @Operation(summary = "All assets in database are returned")
    @ApiResponse(responseCode = "200", description = "Assets returned")
    @GetMapping("/assets")
    public List<Asset> getAssets() {
        return assetService.getAvailableAssets();
    }
}
