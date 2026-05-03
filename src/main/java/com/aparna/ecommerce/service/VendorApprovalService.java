package com.aparna.ecommerce.service;

import com.aparna.ecommerce.entity.Product;
import com.aparna.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorApprovalService {

    private final ProductRepository productRepository;
    //private final ProductSearchService productSearchService;

    @Transactional
    public Product approveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setActive(true);
        Product saved = productRepository.save(product);

        //productSearchService.indexProduct(saved);
        log.info("Product {} approved and indexed", productId);
        return saved;
    }

    @Transactional
    public Product rejectProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        product.setActive(false);
        Product saved = productRepository.save(product);

        //productSearchService.removeFromIndex(productId);
        log.info("Product {} deactivated and removed from index", productId);
        return saved;
    }
}