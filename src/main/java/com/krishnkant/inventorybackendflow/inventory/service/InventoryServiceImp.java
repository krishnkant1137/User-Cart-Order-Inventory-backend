package com.krishnkant.inventorybackendflow.inventory.service;

import com.krishnkant.inventorybackendflow.exception.ProductNotFoundException;
import com.krishnkant.inventorybackendflow.inventory.cache.StockCache;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class InventoryServiceImp implements InventoryService {

    private final ProductRepository productRepository;
    private final StockCache stockCache;

    public InventoryServiceImp(ProductRepository productRepository,
                               StockCache stockCache) {
        this.productRepository = productRepository;
        this.stockCache = stockCache;
    }

    @Override
    public Integer checkStock(Long productId) {

        // 1️⃣ Check Cache First
        Integer cachedStock = stockCache.get(productId);

        if (cachedStock != null) {
            log.debug("Stock fetched from CACHE for productId={}", productId);
            return cachedStock;
        }

        // 2️⃣ Fetch From DB
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        Integer stock = product.getStock();

        // 3️⃣ Store In Cache
        stockCache.put(productId, stock);

        log.info("Stock fetched from DB and cached for productId={}", productId);

        return stock;
    }

    @Override
    public void updateStock(Long productId, Integer newStock) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        product.setStock(newStock);

        // 4️⃣ Update Cache
        stockCache.put(productId, newStock);

        log.info("Stock updated and cache refreshed for productId={}", productId);
    }
}
