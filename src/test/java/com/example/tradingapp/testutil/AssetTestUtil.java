package com.example.tradingapp.testutil;

import com.example.tradingapp.model.Asset;

public class AssetTestUtil {

    public static final String ASSET_TABLE = "Asset";

    public static Asset asset(String symbol, String name) {
        var asset = new Asset();
        asset.setName(name);
        asset.setSymbol(symbol);
        asset.setPrice(12.20);
        return asset;
    }
}
