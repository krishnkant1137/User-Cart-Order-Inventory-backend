package com.krishnkant.inventorybackendflow.order.service;

import com.krishnkant.inventorybackendflow.cart.entity.Cart;
import com.krishnkant.inventorybackendflow.cart.entity.CartItem;
import com.krishnkant.inventorybackendflow.cart.entity.CartStatus;
import com.krishnkant.inventorybackendflow.cart.repository.CartRepository;
import com.krishnkant.inventorybackendflow.discount.service.DiscountService;
import com.krishnkant.inventorybackendflow.exception.CartNotFoundException;
import com.krishnkant.inventorybackendflow.exception.StockNotAvailableException;
import com.krishnkant.inventorybackendflow.order.dto.OrderItemDTO;
import com.krishnkant.inventorybackendflow.order.dto.OrderResponseDTO;
import com.krishnkant.inventorybackendflow.order.entity.Order;
import com.krishnkant.inventorybackendflow.order.entity.OrderItem;
import com.krishnkant.inventorybackendflow.order.entity.OrderStatus;
import com.krishnkant.inventorybackendflow.order.repository.OrderRepository;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.repository.ProductRepository;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.serviceImp.UserServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserServiceImp userServiceImp;
    private final DiscountService discountService;

    public OrderServiceImp(OrderRepository orderRepository,
                           CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserServiceImp userServiceImp,
                           DiscountService discountService) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userServiceImp = userServiceImp;
        this.discountService = discountService;
    }

    public OrderResponseDTO  placeOrder(Long userId, String idempotencyKey) {

        log.info("Placing order for userId={}, reference={}", userId, idempotencyKey);

        Order existingOrder = orderRepository
                .findByOrderReference(idempotencyKey)
                .orElse(null);

        if (existingOrder != null) {
            log.warn("Duplicate order request detected. Returning existing order.");
            return mapToResponse(existingOrder);
        }


        User user = userServiceImp.getActiveUser(userId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("Active cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new CartNotFoundException("Cart is empty");
        }

        double totalAmount = 0;

        for (CartItem cartItem : cart.getItems()) {

            Long productId = cartItem.getProduct().getId();
            Integer quantity = cartItem.getQuantity();

            // 4️⃣ Atomic stock deduction
            int updatedRows = productRepository
                    .deductStockIfAvailable(productId, quantity);

            if (updatedRows == 0) {
                throw new StockNotAvailableException(
                        "Insufficient stock for product id: " + productId);
            }

            double itemTotal = cartItem.getProduct().getPrice() * quantity;
            totalAmount += itemTotal;
        }

        double discountAmount = discountService.applyDiscount(totalAmount);
        double finalAmount = totalAmount - discountAmount;

        Order order = Order.builder()
                .user(user)
                .orderReference(idempotencyKey)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .status(OrderStatus.CREATED)
                .build();



        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(quantity)
                    .totalPrice(product.getPrice() * quantity)
                    .build();

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        cart.setStatus(CartStatus.ORDERED);

        log.info("Order placed successfully with id={}", savedOrder.getId());

        return mapToResponse(savedOrder);
    }
    private OrderResponseDTO mapToResponse(Order order) {

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getOrderReference(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getFinalAmount(),
                order.getStatus().name(),
                itemDTOs
        );
    }

    public Page<OrderResponseDTO> getOrderHistory(
            Long userId,
            int page,
            int size,
            String sortBy,
            String direction) {

        User user = userServiceImp.getActiveUser(userId);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders =
                orderRepository.findByUser(user, pageable);

        return orders.map(this::mapToResponse);
    }


    private void validateTransition(OrderStatus current, OrderStatus newStatus) {

        switch (current) {

            case CREATED -> {
                if (newStatus != OrderStatus.CONFIRMED &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition");
                }
            }

            case CONFIRMED -> {
                if (newStatus != OrderStatus.SHIPPED &&
                        newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("Invalid status transition");
                }
            }

            case SHIPPED -> {
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("Invalid status transition");
                }
            }

            case DELIVERED, CANCELLED ->
                    throw new IllegalStateException("Order cannot be modified");
        }
    }
    public OrderResponseDTO updateStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        validateTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        return mapToResponse(order);
    }


}
