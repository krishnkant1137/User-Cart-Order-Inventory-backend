package com.krishnkant.inventorybackendflow.cart.dto;

public record CartItemRequestDTO(
        Long userId,
        Long productId,
        Integer quantity
) {}
