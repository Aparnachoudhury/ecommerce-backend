package com.aparna.ecommerce.service;

import com.aparna.ecommerce.entity.Product;
import com.aparna.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ✅ GET ALL
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ ADD
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // ✅ UPDATE
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    // ✅ DELETE
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // ✅ SEARCH (SAFE VERSION)
    public List<Product> searchProducts(String q, Double minPrice, Double maxPrice) {

        return productRepository.findAll().stream()

                // 🔍 search
                .filter(p -> q == null ||
                        p.getName().toLowerCase().contains(q.toLowerCase()) ||
                        (p.getDescription() != null &&
                                p.getDescription().toLowerCase().contains(q.toLowerCase()))
                )

                // 💰 min price
                .filter(p -> minPrice == null ||
                        (p.getBasePrice() != null &&
                                p.getBasePrice().doubleValue() >= minPrice))

                // 💰 max price
                .filter(p -> maxPrice == null ||
                        (p.getBasePrice() != null &&
                                p.getBasePrice().doubleValue() <= maxPrice))

                .toList();
    }

    // ✅ AUTOCOMPLETE
    public List<String> autocomplete(String q) {
        return productRepository.findAll().stream()
                .map(Product::getName)
                .filter(name -> name.toLowerCase().startsWith(q.toLowerCase()))
                .limit(5)
                .toList();
    }
}