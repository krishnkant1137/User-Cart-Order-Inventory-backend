package com.krishnkant.inventorybackendflow.cart.service;

import com.krishnkant.inventorybackendflow.cart.dto.CartItemResponseDTO;
import com.krishnkant.inventorybackendflow.cart.dto.CartResponseDTO;
import com.krishnkant.inventorybackendflow.cart.entity.*;
import com.krishnkant.inventorybackendflow.cart.repository.*;
import com.krishnkant.inventorybackendflow.exception.CartNotFoundException;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.service.ProductServiceImp;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.serviceImp.UserServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class CartServiceImp implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceImp productServiceImp;
    private final UserServiceImp userServiceImp;

    public CartServiceImp(CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          ProductServiceImp productServiceImp,
                          UserServiceImp userServiceImp) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productServiceImp = productServiceImp;
        this.userServiceImp = userServiceImp;
    }

    public CartResponseDTO addToCart(Long userId,
                                     Long productId,
                                     Integer quantity) {

        log.info("Add to cart request userId={}, productId={}, quantity={}",
                userId, productId, quantity);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userServiceImp.getActiveUser(userId);
        Product product = productServiceImp.getActiveProduct(productId);

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
            cart.getItems().add(newItem);
        }

        log.info("Product added to cart successfully");

        return mapToResponse(cart);
    }

    public CartResponseDTO viewCart(Long userId) {

        User user = userServiceImp.getActiveUser(userId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new CartNotFoundException("Active cart not found"));

        return mapToResponse(cart);
    }

    public void removeItem(Long userId, Long productId) {

        User user = userServiceImp.getActiveUser(userId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new CartNotFoundException("Active cart not found"));

        cart.getItems().removeIf(
                item -> item.getProduct().getId().equals(productId)
        );
    }

    private CartResponseDTO mapToResponse(Cart cart) {

        List<CartItemResponseDTO> items =
                cart.getItems().stream()
                        .map(item -> new CartItemResponseDTO(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getProduct().getPrice(),
                                item.getQuantity(),
                                item.getProduct().getPrice()
                                        * item.getQuantity()
                        ))
                        .toList();

        Double total = items.stream()
                .mapToDouble(CartItemResponseDTO::totalPrice)
                .sum();

        return new CartResponseDTO(
                cart.getId(),
                cart.getStatus().name(),
                items,
                total
        );
    }


}
