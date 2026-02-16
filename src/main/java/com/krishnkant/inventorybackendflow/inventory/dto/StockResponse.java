package com.krishnkant.inventorybackendflow.inventory.dto;

public record StockResponse(
        Long productId,
        Integer availableStock
) {}

