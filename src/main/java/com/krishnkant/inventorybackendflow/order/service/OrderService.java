package com.krishnkant.inventorybackendflow.order.service;

import com.krishnkant.inventorybackendflow.order.dto.OrderResponseDTO;
import com.krishnkant.inventorybackendflow.order.entity.OrderStatus;
import org.springframework.data.domain.Page;

public interface OrderService {
    public OrderResponseDTO placeOrder(Long userId, String idempotencyKey);
    public Page<OrderResponseDTO> getOrderHistory(
            Long userId,
            int page,
            int size,
            String sortBy,
            String direction);

    public OrderResponseDTO updateStatus(Long orderId, OrderStatus newStatus);
    OrderResponseDTO payOrder(Long orderId);
}
