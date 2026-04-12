package com.example.tradingapp.repository;

import com.example.tradingapp.model.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {

    List<Asset> getAvailableAssets();
    Optional<Asset> getAsset(String symbol);
}
