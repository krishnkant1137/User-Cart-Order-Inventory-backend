package com.krishnkant.inventorybackendflow.cart.controller;

import com.krishnkant.inventorybackendflow.cart.dto.CartResponseDTO;
import com.krishnkant.inventorybackendflow.cart.service.CartService;
import com.krishnkant.inventorybackendflow.cart.service.CartServiceImp;
import com.krishnkant.inventorybackendflow.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartServiceImp;

    public CartController(CartServiceImp cartServiceImp) {
        this.cartServiceImp = cartServiceImp;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        CartResponseDTO response =
                cartServiceImp.addToCart(userId, productId, quantity);

        return ResponseEntity.ok(
                ApiResponse.success(response,
                        "Item added to cart successfully",
                        200)
        );
    }

    @GetMapping("/view")
    public ResponseEntity<ApiResponse<CartResponseDTO>> viewCart(
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        cartServiceImp.viewCart(userId),
                        "Cart fetched successfully",
                        200
                )
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeItem(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        cartServiceImp.removeItem(userId, productId);

        return ResponseEntity.ok(
                ApiResponse.success("Item removed",
                        "Removed successfully",
                        200)
        );
    }
}

