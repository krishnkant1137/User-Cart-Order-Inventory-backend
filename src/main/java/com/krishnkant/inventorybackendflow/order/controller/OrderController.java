package com.krishnkant.inventorybackendflow.order.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.order.dto.OrderResponseDTO;
import com.krishnkant.inventorybackendflow.order.entity.OrderStatus;
import com.krishnkant.inventorybackendflow.order.service.OrderServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderServiceImp orderServiceImp;

    public OrderController(OrderServiceImp orderServiceImp) {
        this.orderServiceImp = orderServiceImp;
    }

    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrder(
            @RequestParam Long userId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        OrderResponseDTO response =
                orderServiceImp.placeOrder(userId, idempotencyKey);

        return ResponseEntity.ok(
                ApiResponse.success(response,
                        "Order placed successfully",
                        200)
        );
    }


    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<OrderResponseDTO>>> getOrderHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<OrderResponseDTO> history =
                orderServiceImp.getOrderHistory(userId, page, size, sortBy, direction);

        return ResponseEntity.ok(
                ApiResponse.success(history,
                        "Order history fetched successfully",
                        200)
        );
    }
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus) {

        OrderResponseDTO response =
                orderServiceImp.updateStatus(orderId, newStatus);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Order status updated successfully",
                        200
                )
        );
    }



}
