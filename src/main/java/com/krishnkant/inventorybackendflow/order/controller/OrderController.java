package com.krishnkant.inventorybackendflow.order.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.order.dto.OrderResponseDTO;
import com.krishnkant.inventorybackendflow.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> placeOrder(
            @RequestParam Long userId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        OrderResponseDTO response =
                orderService.placeOrder(userId, idempotencyKey);

        return ResponseEntity.ok(
                ApiResponse.success(response,
                        "Order placed successfully",
                        200)
        );
    }


    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrderHistory(
            @RequestParam Long userId) {

        List<OrderResponseDTO> history =
                orderService.getOrderHistory(userId);

        return ResponseEntity.ok(
                ApiResponse.success(history,
                        "Order history fetched successfully",
                        200)
        );
    }


}
