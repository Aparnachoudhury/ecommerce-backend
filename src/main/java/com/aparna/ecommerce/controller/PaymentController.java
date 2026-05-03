package com.aparna.ecommerce.controller;

import com.aparna.ecommerce.dto.PaymentRequest;
import com.aparna.ecommerce.entity.Order;
import com.aparna.ecommerce.entity.OrderStatus;
import com.aparna.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderRepository orderRepository;

    // ✅ SIMPLIFIED VERIFY (NO SIGNATURE CHECK)
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentRequest req) {

        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔥 Directly mark as PAID (demo mode)
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return ResponseEntity.ok("✅ Payment successful (demo mode)");
    }

    // 🧪 OPTIONAL: Manual testing without Razorpay
    @PostMapping("/simulate/{orderId}")
    public String simulatePayment(@PathVariable Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return "✅ Payment simulated successfully";
    }
}