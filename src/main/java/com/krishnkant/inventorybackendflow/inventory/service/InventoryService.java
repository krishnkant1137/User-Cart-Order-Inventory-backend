package com.krishnkant.inventorybackendflow.inventory.service;

public interface InventoryService {
    public Integer checkStock(Long productId);
    public void updateStock(Long productId, Integer newStock);
    }
