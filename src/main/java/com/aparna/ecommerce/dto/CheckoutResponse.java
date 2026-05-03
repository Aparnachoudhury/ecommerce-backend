package com.aparna.ecommerce.dto;

public class CheckoutResponse {
    public Long orderId;
    public String paymentIntentId;
    public double totalAmount;
    public String status;
}