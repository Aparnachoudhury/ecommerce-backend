package com.aparna.ecommerce.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    public String createPaymentIntent() {
        return "PAY_" + UUID.randomUUID();
    }
}