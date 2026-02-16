package com.krishnkant.inventorybackendflow.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(
        Long cartId,
        String status,
        List<CartItemResponseDTO> items,
        BigDecimal totalAmount
) {}
