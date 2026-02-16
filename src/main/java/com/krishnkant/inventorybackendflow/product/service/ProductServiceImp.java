package com.krishnkant.inventorybackendflow.product.service;

import com.krishnkant.inventorybackendflow.exception.ProductNotFoundException;
import com.krishnkant.inventorybackendflow.product.dto.ProductRequestDTO;
import com.krishnkant.inventorybackendflow.product.dto.ProductResponseDTO;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import com.krishnkant.inventorybackendflow.product.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImp(ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    public Product getActiveProduct(Long productId) {

        log.info("Fetching product with id: {}", productId);

        return productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));
    }
    public ProductResponseDTO create(ProductRequestDTO dto) {

        log.info("Creating product with name={}", dto.name());

        Product product = Product.builder()
                .name(dto.name())
                .price(dto.price())
                .stock(dto.stock())
                .active(true)
                .build();

        Product saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    public Page<ProductResponseDTO> getAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    public ProductResponseDTO getById(Long id) {

        Product product = getActiveProduct(id);

        return mapToResponse(product);
    }

    public void softDelete(Long id) {

        Product product = getActiveProduct(id);

        product.setActive(false);
    }

    private ProductResponseDTO mapToResponse(Product product) {

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getActive()
        );
    }

}
