package com.krishnkant.inventorybackendflow.cart.repository;

import com.krishnkant.inventorybackendflow.cart.entity.Cart;
import com.krishnkant.inventorybackendflow.cart.entity.CartStatus;
import com.krishnkant.inventorybackendflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndStatus(User user, CartStatus status);
}
