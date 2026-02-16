package com.krishnkant.inventorybackendflow.cart.service;

import com.krishnkant.inventorybackendflow.cart.dto.CartResponseDTO;

public interface CartService {
    public CartResponseDTO addToCart(Long userId,
                                     Long productId,
                                     Integer quantity);

    public CartResponseDTO viewCart(Long userId);

    public void removeItem(Long userId, Long productId);


    }
