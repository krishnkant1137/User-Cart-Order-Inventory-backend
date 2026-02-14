package com.krishnkant.inventorybackendflow.inventory.service;

import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InventoryService {

    private final ProductRepository productRepository;

    // Simple in-memory cache
    private final ConcurrentHashMap<Long, Integer> stockCache =
            new ConcurrentHashMap<>();

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 1️⃣ Check stock
    public Integer checkStock(Long productId) {

        // Check cache first
        if (stockCache.containsKey(productId)) {
            log.info("Stock fetched from cache for productId={}", productId);
            return stockCache.get(productId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        stockCache.put(productId, product.getStock());

        log.info("Stock loaded from DB and cached");

        return product.getStock();
    }

    // 2️⃣ Update stock (Admin)
    public void updateStock(Long productId, Integer newStock) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        product.setStock(newStock);
        productRepository.save(product);

        // Update cache
        stockCache.put(productId, newStock);

        log.info("Stock updated for productId={}", productId);
    }
}
