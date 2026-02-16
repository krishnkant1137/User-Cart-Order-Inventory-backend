package com.krishnkant.inventorybackendflow.inventory.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.inventory.dto.StockResponse;
import com.krishnkant.inventorybackendflow.inventory.dto.StockUpdateResponse;
import com.krishnkant.inventorybackendflow.inventory.service.InventoryServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryServiceImp inventoryServiceImp;

    public InventoryController(InventoryServiceImp inventoryServiceImp) {
        this.inventoryServiceImp = inventoryServiceImp;
    }

    @GetMapping("/stock/{productId}")
    public ResponseEntity<ApiResponse<StockResponse>> checkStock(
            @PathVariable Long productId) {

        Integer stock = inventoryServiceImp.checkStock(productId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        new StockResponse(productId, stock),
                        "Stock fetched successfully",
                        HttpStatus.CREATED
                )
        );
    }


    @PutMapping("/stock/{productId}")
    public ResponseEntity<ApiResponse<StockUpdateResponse>> updateStock(
            @PathVariable Long productId,
            @RequestParam Integer newStock) {

        inventoryServiceImp.updateStock(productId, newStock);

        StockUpdateResponse response =
                new StockUpdateResponse(productId, newStock);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Stock updated successfully",
                        HttpStatus.CREATED
                )
        );
    }

}
