package com.krishnkant.inventorybackendflow.cart.service;

import com.krishnkant.inventorybackendflow.cart.entity.*;
import com.krishnkant.inventorybackendflow.cart.repository.*;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.service.ProductService;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductService productService,
                       UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.userService = userService;
    }

    public void addToCart(Long userId, Long productId, Integer quantity) {

        log.info("Add to cart request userId={}, productId={}, quantity={}",
                userId, productId, quantity);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userService.getActiveUser(userId);

        Product product = productService.getActiveProduct(productId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .status(CartStatus.ACTIVE)
                            .build();
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cartItemRepository.save(newItem);
        }

        log.info("Product added to cart successfully");
    }
}
