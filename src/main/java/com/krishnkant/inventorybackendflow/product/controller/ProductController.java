package com.krishnkant.inventorybackendflow.product.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.product.dto.ProductRequestDTO;
import com.krishnkant.inventorybackendflow.product.dto.ProductResponseDTO;
import com.krishnkant.inventorybackendflow.product.service.ProductService;
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

    private final ProductService productService;

    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(
            @Valid @RequestBody ProductRequestDTO dto) {

        ProductResponseDTO response = productService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product created successfully", HttpStatus.CREATED));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ProductResponseDTO> products =
                productService.getAll(page, size);

        return ResponseEntity.ok(
                ApiResponse.success(products,
                        "Products fetched successfully", HttpStatus.OK)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getById(id),
                        "Product fetched successfully",HttpStatus.OK
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(
            @PathVariable Long id) {

        productService.softDelete(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully",
                        "Deleted",HttpStatus.OK)
        );
    }
}
