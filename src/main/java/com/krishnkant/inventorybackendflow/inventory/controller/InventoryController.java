package com.krishnkant.inventorybackendflow.inventory.controller;

import com.krishnkant.inventorybackendflow.inventory.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Check stock
    @GetMapping("/stock/{productId}")
    public ResponseEntity<Integer> checkStock(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryService.checkStock(productId));
    }

    // Update stock (admin)
    @PutMapping("/stock/{productId}")
    public ResponseEntity<String> updateStock(
            @PathVariable Long productId,
            @RequestParam Integer newStock) {

        inventoryService.updateStock(productId, newStock);

        return ResponseEntity.ok("Stock updated successfully");
    }
}
