package com.aparna.ecommerce.service;

import com.aparna.ecommerce.entity.Product;
import org.springframework.stereotype.Component;
import com.aparna.ecommerce.document.ProductDocument;


@Component
public class ProductMapper {

    public ProductDocument toDocument(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(product.getId().toString());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setBasePrice(product.getBasePrice());
        doc.setActive(product.isActive());
        doc.setCreatedAt(product.getCreatedAt());
        doc.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            doc.setCategory(product.getCategory().getName()); // adjust if your Category field is different
        }
        if (product.getVendor() != null) {
            doc.setVendor(product.getVendor().getId().toString());
            doc.setVendor(product.getVendor().getBusinessName());
        }
        return doc;
    }
}