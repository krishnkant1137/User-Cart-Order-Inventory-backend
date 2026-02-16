package com.krishnkant.inventorybackendflow.order.service;

import com.krishnkant.inventorybackendflow.cart.entity.Cart;
import com.krishnkant.inventorybackendflow.cart.entity.CartItem;
import com.krishnkant.inventorybackendflow.cart.entity.CartStatus;
import com.krishnkant.inventorybackendflow.cart.repository.CartRepository;
import com.krishnkant.inventorybackendflow.discount.service.DiscountService;
import com.krishnkant.inventorybackendflow.exception.CartNotFoundException;
import com.krishnkant.inventorybackendflow.exception.OrderNotFoundException;
import com.krishnkant.inventorybackendflow.exception.StockNotAvailableException;
import com.krishnkant.inventorybackendflow.invoice.entity.Invoice;
import com.krishnkant.inventorybackendflow.invoice.repository.InvoiceRepository;
import com.krishnkant.inventorybackendflow.order.dto.OrderItemDTO;
import com.krishnkant.inventorybackendflow.order.dto.OrderResponseDTO;
import com.krishnkant.inventorybackendflow.order.entity.Order;
import com.krishnkant.inventorybackendflow.order.entity.OrderItem;
import com.krishnkant.inventorybackendflow.order.entity.OrderStatus;
import com.krishnkant.inventorybackendflow.order.entity.PaymentStatus;
import com.krishnkant.inventorybackendflow.order.repository.OrderRepository;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.repository.ProductRepository;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final DiscountService discountService;
    private final InvoiceRepository invoiceRepository;

    public OrderServiceImp(OrderRepository orderRepository,
                           CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserService userService,
                           InvoiceRepository invoiceRepository,
                           DiscountService discountService) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.discountService = discountService;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public OrderResponseDTO placeOrder(Long userId, String idempotencyKey) {

        Order existingOrder =
                orderRepository.findByOrderReference(idempotencyKey)
                        .orElse(null);

        if (existingOrder != null) {
            return mapToResponse(existingOrder);
        }

        User user = userService.getActiveUser(userId);

        Cart cart = cartRepository
                .findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() ->
                        new CartNotFoundException("Active cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new CartNotFoundException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Long productId = cartItem.getProduct().getId();
            Integer quantity = cartItem.getQuantity();

            int updatedRows =
                    productRepository.deductStockIfAvailable(productId, quantity);

            if (updatedRows == 0) {
                throw new StockNotAvailableException(
                        "Insufficient stock for product id: " + productId);
            }

            BigDecimal itemTotal =
                    cartItem.getProduct()
                            .getPrice()
                            .multiply(BigDecimal.valueOf(quantity));

            totalAmount = totalAmount.add(itemTotal);
        }

        BigDecimal discountAmount =
                discountService.applyDiscount(totalAmount);

        BigDecimal finalAmount =
                totalAmount.subtract(discountAmount);

        Order order = Order.builder()
                .user(user)
                .orderReference(idempotencyKey)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .status(OrderStatus.CREATED)
                .paymentStatus(PaymentStatus.UNPAID)
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
                    .totalPrice(
                            product.getPrice()
                                    .multiply(BigDecimal.valueOf(quantity))
                    )
                    .build();

            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        cart.setStatus(CartStatus.ORDERED);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> getOrderHistory(
            Long userId,
            int page,
            int size,
            String sortBy,
            String direction) {

        User user = userService.getActiveUser(userId);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders =
                orderRepository.findByUser(user, pageable);

        return orders.map(this::mapToResponse);
    }

    @Override
    public OrderResponseDTO updateStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        validateTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        return mapToResponse(order);
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

    private OrderResponseDTO mapToResponse(Order order) {

        List<OrderItemDTO> items =
                order.getItems().stream()
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
                items
        );
    }

    @Override
    public OrderResponseDTO payOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return mapToResponse(order);
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTransactionId("TXN-" + UUID.randomUUID());
        order.setPaidAt(LocalDateTime.now());

        generateInvoice(order);

        return mapToResponse(order);
    }

    private void generateInvoice(Order order) {

        Invoice invoice = Invoice.builder()
                .order(order)
                .invoiceNumber("INV-" + UUID.randomUUID())
                .amount(order.getFinalAmount())
                .issuedAt(LocalDateTime.now())
                .build();

        invoiceRepository.save(invoice);
    }


}
