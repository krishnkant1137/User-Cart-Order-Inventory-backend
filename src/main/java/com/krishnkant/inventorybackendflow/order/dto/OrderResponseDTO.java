package com.krishnkant.inventorybackendflow.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDTO(
        Long orderId,
        String orderReference,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String status,
        List<OrderItemDTO> items
) {}
