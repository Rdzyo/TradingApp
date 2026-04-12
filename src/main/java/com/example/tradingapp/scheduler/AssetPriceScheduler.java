package com.example.tradingapp.scheduler;

import com.example.tradingapp.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.random.RandomGenerator;

@Component
@Slf4j
@RequiredArgsConstructor
public class AssetPriceScheduler {

    private final AssetRepository assetRepository;

    @Scheduled(cron = "${asset.price.scheduler.cron}")
    public void changeAssetPriceTask() {
        log.info("Started scheduled task to change prices of assets");
        var assets = assetRepository.getAvailableAssets();
        assets.forEach(asset ->
                asset.setPrice(new BigDecimal(RandomGenerator.getDefault().nextDouble(300.00)+100).setScale(2, RoundingMode.CEILING).doubleValue()));
        assetRepository.updateAssetList(assets);
        log.info("Prices successfully updated");
    }
}
