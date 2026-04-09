package com.example.tradingapp.service;

import com.example.tradingapp.entity.Asset;

import java.util.List;

public interface AssetService {

    List<Asset> getAvailableAssets();
}
