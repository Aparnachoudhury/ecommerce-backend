package com.aparna.ecommerce.service;

import com.aparna.ecommerce.entity.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductSearchService {

    public void indexProduct(Product product) {
        // TODO: send product to Elasticsearch
        System.out.println("Indexing product: " + product.getName());
    }

    public void removeFromIndex(Long productId) {
        // TODO: remove from Elasticsearch
        System.out.println("Removing product: " + productId);
    }
}