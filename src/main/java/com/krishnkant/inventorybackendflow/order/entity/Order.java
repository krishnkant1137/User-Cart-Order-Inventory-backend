package com.krishnkant.inventorybackendflow.order.entity;

import com.krishnkant.inventorybackendflow.common.BaseEntity;
import com.krishnkant.inventorybackendflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user placed this order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Unique reference for idempotency
    @Column(nullable = false, unique = true)
    private String orderReference;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private Double finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();
}
