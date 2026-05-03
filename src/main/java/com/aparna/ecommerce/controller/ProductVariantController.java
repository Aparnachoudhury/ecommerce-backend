package com.aparna.ecommerce.controller;

import com.aparna.ecommerce.entity.Product;
import com.aparna.ecommerce.entity.ProductVariant;
import com.aparna.ecommerce.repository.ProductRepository;
import com.aparna.ecommerce.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantRepository repository;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> createVariant(@RequestBody ProductVariant variant) {

        // 🔥 attach product manually
        Product product = productRepository.findById(
                variant.getProduct().getId()
        ).orElseThrow(() -> new RuntimeException("Product not found"));

        variant.setProduct(product);

        return ResponseEntity.ok(repository.save(variant));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }
}