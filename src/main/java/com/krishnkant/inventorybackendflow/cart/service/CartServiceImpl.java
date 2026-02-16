package com.krishnkant.inventorybackendflow.cart.service;

import com.krishnkant.inventorybackendflow.cart.dto.CartItemResponseDTO;
import com.krishnkant.inventorybackendflow.cart.dto.CartResponseDTO;
import com.krishnkant.inventorybackendflow.cart.entity.*;
import com.krishnkant.inventorybackendflow.cart.repository.*;
import com.krishnkant.inventorybackendflow.exception.CartNotFoundException;
import com.krishnkant.inventorybackendflow.exception.StockNotAvailableException;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.service.ProductService;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductService productService,
                           UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public CartResponseDTO addToCart(Long userId,
                                     Long productId,
                                     Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        User user = userService.getActiveUser(userId);
        Product product = productService.getActiveProduct(productId);

        if (product.getStock() < quantity) {
            throw new StockNotAvailableException("Insufficient stock available");
        }

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseGet(() ->
                        cartRepository.save(
                                Cart.builder()
                                        .user(user)
                                        .status(CartStatus.ACTIVE)
                                        .build()
                        )
                );

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

        return mapToResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO viewCart(Long userId) {

        User user = userService.getActiveUser(userId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new CartNotFoundException("Active cart not found"));

        return mapToResponse(cart);
    }

    @Override
    public void removeItem(Long userId, Long productId) {

        User user = userService.getActiveUser(userId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new CartNotFoundException("Active cart not found"));

        boolean removed = cart.getItems()
                .removeIf(item ->
                        item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new CartNotFoundException("Product not found in cart");
        }
    }

    private CartResponseDTO mapToResponse(Cart cart) {

        List<CartItemResponseDTO> items =
                cart.getItems().stream()
                        .map(item -> {
                            BigDecimal total =
                                    item.getProduct().getPrice()
                                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                            return new CartItemResponseDTO(
                                    item.getProduct().getId(),
                                    item.getProduct().getName(),
                                    item.getProduct().getPrice(),
                                    item.getQuantity(),
                                    total
                            );
                        })
                        .toList();

        BigDecimal total =
                items.stream()
                        .map(CartItemResponseDTO::totalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDTO(
                cart.getId(),
                cart.getStatus().name(),
                items,
                total
        );
    }
}
