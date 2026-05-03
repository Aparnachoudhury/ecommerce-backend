package com.aparna.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Basic Info
    @Column(nullable = false)
    private String name;

    private String description;

    // ✅ Relationships
    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private User vendor;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // ✅ Pricing
    @Column(nullable = false)
    private BigDecimal basePrice;

    // ✅ Status
    private boolean isActive = true;

    // ✅ Timestamps
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}