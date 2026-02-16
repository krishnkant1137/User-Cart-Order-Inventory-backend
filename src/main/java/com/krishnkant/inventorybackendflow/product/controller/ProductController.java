package com.krishnkant.inventorybackendflow.product.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.product.dto.ProductRequestDTO;
import com.krishnkant.inventorybackendflow.product.dto.ProductResponseDTO;
import com.krishnkant.inventorybackendflow.product.service.ProductServiceImp;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductServiceImp productServiceImp;

    public ProductController(ProductServiceImp productServiceImp) {
        this.productServiceImp = productServiceImp;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(
            @Valid @RequestBody ProductRequestDTO dto) {

        ProductResponseDTO response = productServiceImp.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response,
                        "Product created successfully",
                        201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ProductResponseDTO> products =
                productServiceImp.getAll(page, size);

        return ResponseEntity.ok(
                ApiResponse.success(products,
                        "Products fetched successfully",
                        200)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productServiceImp.getById(id),
                        "Product fetched successfully",
                        200
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        productServiceImp.softDelete(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully",
                        "Deleted",
                        200)
        );
    }
}
