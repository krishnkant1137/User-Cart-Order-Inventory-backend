package com.krishnkant.inventorybackendflow.inventory.service;

import com.krishnkant.inventorybackendflow.inventory.cache.StockCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryStockCache implements StockCache {

    private final ConcurrentHashMap<Long, Integer> cache =
            new ConcurrentHashMap<>();

    @Override
    public Integer get(Long productId) {
        return cache.get(productId);
    }

    @Override
    public void put(Long productId, Integer stock) {
        cache.put(productId, stock);
    }

    @Override
    public void evict(Long productId) {
        cache.remove(productId);
    }
}
