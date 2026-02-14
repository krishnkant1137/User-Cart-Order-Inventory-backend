package com.krishnkant.inventorybackendflow.product.service;

import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getActiveProduct(Long productId) {

        log.info("Fetching product with id: {}", productId);

        return productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }
}
