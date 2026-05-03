package com.aparna.ecommerce.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;
    private String businessName;

    @Enumerated(EnumType.STRING)
    private RoleType role;
    private boolean emailVerified = false;
    private boolean enabled = true;
}