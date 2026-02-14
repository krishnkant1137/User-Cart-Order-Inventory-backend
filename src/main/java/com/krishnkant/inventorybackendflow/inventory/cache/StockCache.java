package com.krishnkant.inventorybackendflow.inventory.cache;

public interface StockCache {

    Integer get(Long productId);

    void put(Long productId, Integer stock);

    void evict(Long productId);
}
