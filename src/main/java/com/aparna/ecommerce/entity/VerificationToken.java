package com.aparna.ecommerce.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
public class VerificationToken {

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private Instant expiryDate;
}