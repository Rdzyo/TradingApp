package com.example.tradingapp.repository;

import com.example.tradingapp.entity.Asset;

import java.util.List;
import java.util.Optional;

public interface AssetPort {

    List<Asset> getAvailableAssets();
    Optional<Asset> getAsset(String symbol);
}
