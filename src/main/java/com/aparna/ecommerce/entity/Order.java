package com.aparna.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 📦 Order Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    // 💰 Total Amount
    @Column(nullable = false)
    private BigDecimal totalAmount;

    // 📍 Shipping Address
    private String shippingAddress;

    // 💳 Payment Intent (Stripe simulation)
    private String paymentIntentId;

    // ⏱️ Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 🛒 Order Items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // ⏰ Auto set on create
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ⏰ Auto update
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}