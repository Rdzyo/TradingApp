package com.example.tradingapp.port;

import com.example.tradingapp.model.Asset;
import com.example.tradingapp.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssetPortImpl implements AssetPort {

    private final AssetRepository assetRepository;

    @Override
    public List<Asset> getAvailableAssets() {
        return assetRepository.getAvailableAssets();
    }

    @Override
    public Optional<Asset> getAsset(String symbol) {
        return assetRepository.getAsset(symbol);
    }
}
