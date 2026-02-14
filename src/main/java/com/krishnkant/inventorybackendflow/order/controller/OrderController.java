package com.krishnkant.inventorybackendflow.order.controller;

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
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @RequestParam Long userId,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        log.info("Order request received");

        OrderResponseDTO response =
                orderService.placeOrder(userId, idempotencyKey);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderResponseDTO>> getOrderHistory(
            @RequestParam Long userId) {

        List<OrderResponseDTO> history =
                orderService.getOrderHistory(userId);

        return ResponseEntity.ok(history);
    }

}
