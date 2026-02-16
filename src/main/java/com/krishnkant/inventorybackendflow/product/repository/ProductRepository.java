package com.krishnkant.inventorybackendflow.product.repository;

import com.krishnkant.inventorybackendflow.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndActiveTrue(Long id);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Product p 
        SET p.stock = p.stock - :quantity
        WHERE p.id = :productId 
        AND p.stock >= :quantity
    """)
    int deductStockIfAvailable(Long productId, Integer quantity);

    Page<Product> findByActiveTrue(Pageable pageable);
}

