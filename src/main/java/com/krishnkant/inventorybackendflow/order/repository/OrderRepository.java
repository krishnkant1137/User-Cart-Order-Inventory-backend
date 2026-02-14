package com.krishnkant.inventorybackendflow.order.repository;

import com.krishnkant.inventorybackendflow.order.entity.Order;
import com.krishnkant.inventorybackendflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderReference(String orderReference);
    List<Order> findByUserOrderByCreatedAtDesc(User user);

}
