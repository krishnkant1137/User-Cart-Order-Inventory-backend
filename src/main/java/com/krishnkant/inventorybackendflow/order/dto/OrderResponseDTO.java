package com.krishnkant.inventorybackendflow.order.dto;

import java.util.List;

public record OrderResponseDTO(
        Long orderId,
        String orderReference,
        Double totalAmount,
        Double discountAmount,
        Double finalAmount,
        String status,
        List<OrderItemDTO> items
) {}
