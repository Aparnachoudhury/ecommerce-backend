package com.aparna.ecommerce.controller;

import com.aparna.ecommerce.dto.VendorApprovalRequest;
import com.aparna.ecommerce.dto.VendorOnboardingRequest;
import com.aparna.ecommerce.entity.Vendor;
import com.aparna.ecommerce.entity.VendorApprovalQueue;
import com.aparna.ecommerce.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    // Vendor submits KYC
    @PostMapping("/api/vendor/onboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Vendor> onboard(
            @RequestBody VendorOnboardingRequest request) {
        return ResponseEntity.status(201)
                .body(vendorService.onboard(request));
    }

    // Vendor checks own status
    @GetMapping("/api/vendor/status")
    @PreAuthorize("hasRole('VENDOR') or hasRole('CUSTOMER')")
    public ResponseEntity<Vendor> myStatus() {
        return ResponseEntity.ok(vendorService.getMyVendorStatus());
    }

    // Admin sees pending queue
    @GetMapping("/api/admin/vendors/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VendorApprovalQueue>> pending() {
        return ResponseEntity.ok(
                vendorService.getPendingApprovals());
    }

    // Admin approves
    @PutMapping("/api/admin/vendors/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vendor> approve(
            @PathVariable Long id,
            @RequestBody VendorApprovalRequest request) {
        return ResponseEntity.ok(
                vendorService.approveVendor(id, request));
    }

    // Admin rejects
    @PutMapping("/api/admin/vendors/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vendor> reject(
            @PathVariable Long id,
            @RequestBody VendorApprovalRequest request) {
        return ResponseEntity.ok(
                vendorService.rejectVendor(id, request));
    }
}