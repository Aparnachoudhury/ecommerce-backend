package com.aparna.ecommerce.service;

import com.aparna.ecommerce.dto.VendorApprovalRequest;
import com.aparna.ecommerce.dto.VendorOnboardingRequest;
import com.aparna.ecommerce.entity.*;
import com.aparna.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorService {

    private final VendorRepository vendorRepository;
    private final VendorApprovalQueueRepository approvalQueueRepository;
    private final UserRepository userRepository;

    // ── VENDOR: Submit KYC ────────────────────────────────────────
    @Transactional
    public Vendor onboard(VendorOnboardingRequest request) {
        User user = getCurrentUser();

        if (vendorRepository.existsByUser(user)) {
            throw new RuntimeException(
                    "Vendor profile already exists for this user");
        }

        Vendor vendor = new Vendor();
        vendor.setUser(user);
        vendor.setBusinessName(request.getBusinessName());
        vendor.setPhone(request.getPhone());
        vendor.setAddress(request.getAddress());
        vendor.setPanNumber(request.getPanNumber());
        vendor.setGstNumber(request.getGstNumber());
        vendor.setKycStatus("PENDING");
        vendorRepository.save(vendor);

        // Add to approval queue
        VendorApprovalQueue queue = new VendorApprovalQueue();
        queue.setVendor(vendor);
        queue.setStatus("PENDING");
        queue.setSubmittedAt(LocalDateTime.now());
        approvalQueueRepository.save(queue);

        log.info("Vendor onboarding submitted for user: {}",
                user.getEmail());
        return vendor;
    }

    // ── VENDOR: Check own KYC status ──────────────────────────────
    public Vendor getMyVendorStatus() {
        User user = getCurrentUser();
        return vendorRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("No vendor profile found"));
    }

    // ── ADMIN: Get all pending vendors ────────────────────────────
    public List<VendorApprovalQueue> getPendingApprovals() {
        return approvalQueueRepository.findByStatus("PENDING");
    }

    // ── ADMIN: Approve vendor ─────────────────────────────────────
    @Transactional
    public Vendor approveVendor(Long vendorId,
                                VendorApprovalRequest request) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        vendor.setKycStatus("APPROVED");
        vendor.setUpdatedAt(LocalDateTime.now());

        // Update user role to VENDOR
        User user = vendor.getUser();
        user.setRole(RoleType.VENDOR);
        userRepository.save(user);

        updateQueue(vendorId, "APPROVED", request.getNotes());
        vendorRepository.save(vendor);

        log.info("Vendor approved: {}", vendor.getBusinessName());
        return vendor;
    }

    // ── ADMIN: Reject vendor ──────────────────────────────────────
    @Transactional
    public Vendor rejectVendor(Long vendorId,
                               VendorApprovalRequest request) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found"));

        vendor.setKycStatus("REJECTED");
        vendor.setKycRejectionReason(request.getNotes());
        vendor.setUpdatedAt(LocalDateTime.now());

        updateQueue(vendorId, "REJECTED", request.getNotes());
        vendorRepository.save(vendor);

        log.info("Vendor rejected: {}", vendor.getBusinessName());
        return vendor;
    }

    // ── HELPER ────────────────────────────────────────────────────
    private void updateQueue(Long vendorId, String status,
                             String notes) {
        approvalQueueRepository.findByVendorId(vendorId)
                .ifPresent(q -> {
                    q.setStatus(status);
                    q.setNotes(notes);
                    q.setReviewedAt(LocalDateTime.now());
                    q.setReviewedBy(getCurrentUser());
                    approvalQueueRepository.save(q);
                });
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}