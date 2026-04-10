package com.aparna.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long productId;
    private Long variantId;
    private String productName;
    private String variantName;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}