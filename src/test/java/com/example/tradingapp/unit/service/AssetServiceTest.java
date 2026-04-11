package com.example.tradingapp.unit.service;

import com.example.tradingapp.repository.AssetPort;
import com.example.tradingapp.service.AssetServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.tradingapp.testutil.AssetTestUtil.asset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    AssetPort assetPort;

    @InjectMocks
    AssetServiceImpl assetService;

    @Test
    void shouldReturnListOfAssets() {
        //given
        var symbol1 = "SYMBOL1";
        var symbol2 = "SYMBOL2";
        var name1 = "TEST_NAME1";
        var name2 = "TEST_NAME2";
        when(assetPort.getAvailableAssets())
                .thenReturn(List.of(
                        asset(symbol1, name1),
                        asset(symbol2, name2)));

        //when
        var assets = assetService.getAvailableAssets();

        //then
        assertEquals(2, assets.size());
        assertEquals(symbol1, assets.get(0).getSymbol());
        assertEquals(name1, assets.get(0).getName());
        assertEquals(symbol2, assets.get(1).getSymbol());
        assertEquals(name2, assets.get(1).getName());
    }


}
