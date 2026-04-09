package com.aparna.ecommerce.repository;

import com.aparna.ecommerce.entity.User;
import com.aparna.ecommerce.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUser(User user);
    boolean existsByUser(User user);
}