package com.krishnkant.inventorybackendflow.inventory.dto;

public record StockUpdateResponse(
        Long productId,
        Integer updatedStock
) {}
