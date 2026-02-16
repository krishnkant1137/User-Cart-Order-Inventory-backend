package com.krishnkant.inventorybackendflow.product.service;

import com.krishnkant.inventorybackendflow.product.dto.ProductRequestDTO;
import com.krishnkant.inventorybackendflow.product.dto.ProductResponseDTO;
import com.krishnkant.inventorybackendflow.product.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    public Product getActiveProduct(Long productId);
    public ProductResponseDTO create(ProductRequestDTO dto);
    public Page<ProductResponseDTO> getAll(int page, int size);
    public ProductResponseDTO getById(Long id);
    public void softDelete(Long id);
    }
