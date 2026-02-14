package com.krishnkant.inventorybackendflow.order.dto;

public record OrderItemDTO(
        Long productId,
        String productName,
        Double price,
        Integer quantity,
        Double totalPrice
) {}
