package com.aparna.ecommerce.repository;

import com.aparna.ecommerce.entity.VendorApprovalQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VendorApprovalQueueRepository
        extends JpaRepository<VendorApprovalQueue, Long> {
    List<VendorApprovalQueue> findByStatus(String status);
    Optional<VendorApprovalQueue> findByVendorId(Long vendorId);
}