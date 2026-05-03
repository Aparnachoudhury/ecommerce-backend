package com.aparna.ecommerce.event;

import com.aparna.ecommerce.entity.Product;

public record ProductUpdatedEvent(Product product) {
}