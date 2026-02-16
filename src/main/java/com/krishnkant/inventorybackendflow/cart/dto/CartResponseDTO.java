package com.krishnkant.inventorybackendflow.cart.dto;

import java.util.List;

public record CartResponseDTO(
        Long cartId,
        String status,
        List<CartItemResponseDTO> items,
        Double totalAmount
) {}
