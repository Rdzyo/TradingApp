package com.example.tradingapp.service;

import com.example.tradingapp.model.Asset;
import com.example.tradingapp.repository.AssetPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetPort assetPort;

    public List<Asset> getAvailableAssets() {
        return assetPort.getAvailableAssets();
    }

}
