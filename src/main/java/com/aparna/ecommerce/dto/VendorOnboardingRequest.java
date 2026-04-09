package com.aparna.ecommerce.dto;

import lombok.Data;

@Data
public class VendorOnboardingRequest {
    private String businessName;
    private String phone;
    private String address;
    private String panNumber;
    private String gstNumber;
}