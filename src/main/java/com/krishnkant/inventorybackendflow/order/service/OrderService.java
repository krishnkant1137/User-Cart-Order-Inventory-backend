package com.krishnkant.inventorybackendflow.order.service;

import com.krishnkant.inventorybackendflow.cart.entity.Cart;
import com.krishnkant.inventorybackendflow.cart.entity.CartItem;
import com.krishnkant.inventorybackendflow.cart.entity.CartStatus;
import com.krishnkant.inventorybackendflow.cart.repository.CartRepository;
import com.krishnkant.inventorybackendflow.discount.service.DiscountService;
import com.krishnkant.inventorybackendflow.order.dto.OrderItemDTO;
import com.krishnkant.inventorybackendflow.order.dto.OrderResponseDTO;
import com.krishnkant.inventorybackendflow.order.entity.Order;
import com.krishnkant.inventorybackendflow.order.entity.OrderItem;
import com.krishnkant.inventorybackendflow.order.entity.OrderStatus;
import com.krishnkant.inventorybackendflow.order.repository.OrderRepository;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.repository.ProductRepository;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final DiscountService discountService;

    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        ProductRepository productRepository,
                        UserService userService,
                        DiscountService discountService) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userService = userService;
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


        // 2️⃣ Fetch active cart
        User user = userService.getActiveUser(userId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("Active cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double totalAmount = 0;

        // 3️⃣ Loop cart items
        for (CartItem cartItem : cart.getItems()) {

            Long productId = cartItem.getProduct().getId();
            Integer quantity = cartItem.getQuantity();

            // 4️⃣ Atomic stock deduction
            int updatedRows = productRepository
                    .deductStockIfAvailable(productId, quantity);

            if (updatedRows == 0) {
                throw new RuntimeException(
                        "Insufficient stock for product id: " + productId);
            }

            double itemTotal = cartItem.getProduct().getPrice() * quantity;
            totalAmount += itemTotal;
        }

        // 5️⃣ Apply discount
        double discountAmount = discountService.applyDiscount(totalAmount);
        double finalAmount = totalAmount - discountAmount;

        // 6️⃣ Create Order
        Order order = Order.builder()
                .user(user)
                .orderReference(idempotencyKey)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .status(OrderStatus.CONFIRMED)
                .build();

        // 7️⃣ Create OrderItems snapshot
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

        // 8️⃣ Mark cart as ORDERED
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

    public List<OrderResponseDTO> getOrderHistory(Long userId) {

        User user = userService.getActiveUser(userId);

        List<Order> orders =
                orderRepository.findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::mapToResponse)
                .toList();
    }


}
