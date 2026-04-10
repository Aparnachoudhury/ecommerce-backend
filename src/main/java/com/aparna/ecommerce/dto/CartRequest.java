package com.aparna.ecommerce.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Long variantId;
    private int quantity;
}