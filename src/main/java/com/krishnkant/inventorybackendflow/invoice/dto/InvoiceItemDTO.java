package com.krishnkant.inventorybackendflow.invoice.dto;

import java.math.BigDecimal;

public record InvoiceItemDTO(
        Long productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalPrice
) {}
