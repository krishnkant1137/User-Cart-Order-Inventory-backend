package com.krishnkant.inventorybackendflow.cart.controller;

import com.krishnkant.inventorybackendflow.cart.dto.CartResponseDTO;
import com.krishnkant.inventorybackendflow.cart.service.CartService;
import com.krishnkant.inventorybackendflow.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        CartResponseDTO response =
                cartService.addToCart(userId, productId, quantity);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Item added to cart successfully",
                        HttpStatus.OK
                )
        );
    }

    @GetMapping("/view")
    public ResponseEntity<ApiResponse<CartResponseDTO>> viewCart(
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        cartService.viewCart(userId),
                        "Cart fetched successfully",
                        HttpStatus.OK
                )
        );
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeItem(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        cartService.removeItem(userId, productId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Item removed",
                        "Removed successfully",
                        HttpStatus.OK
                )
        );
    }
}
