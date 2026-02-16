package com.krishnkant.inventorybackendflow.cart.dto;

public record CartItemResponseDTO(
        Long productId,
        String productName,
        Double price,
        Integer quantity,
        Double totalPrice
) {}
