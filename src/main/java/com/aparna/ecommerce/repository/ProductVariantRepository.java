package com.aparna.ecommerce.repository;

import com.aparna.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
    java.util.Optional<ProductVariant> findBySku(String sku);
}