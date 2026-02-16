package com.krishnkant.inventorybackendflow.cart.entity;

import com.krishnkant.inventorybackendflow.common.BaseEntity;
import com.krishnkant.inventorybackendflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many carts belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Cart status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartStatus status;

    // One cart has many cart items
    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
}
