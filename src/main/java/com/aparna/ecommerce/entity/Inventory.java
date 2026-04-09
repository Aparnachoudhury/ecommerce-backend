package com.aparna.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private int quantity = 0;
    private int reservedQuantity = 0;
    private int lowStockThreshold = 10;

    private LocalDateTime updatedAt = LocalDateTime.now();
}