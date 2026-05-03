package com.aparna.ecommerce.controller;

import com.aparna.ecommerce.entity.Order;
import com.aparna.ecommerce.entity.OrderStatus;
import com.aparna.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aparna.ecommerce.service.RazorpayService;
import com.aparna.ecommerce.service.RazorpayService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final RazorpayService razorpayService;

    // 🛒 Checkout (Create Order)
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId
    ) {
        String shippingAddress = body.get("shippingAddress");

        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            throw new RuntimeException("Shipping address is required");
        }

        Order order = orderService.createOrder(guestId, shippingAddress);
        return ResponseEntity.ok(order);
    }

    // 📦 Get my orders (USER)
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        return ResponseEntity.ok(orderService.getOrdersByUser(email));
    }


    // 🔍 Get single order
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // 💰 Mark order as paid
    @PutMapping("/{id}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.markAsPaid(id));
    }

    // 🔄 Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status)
        );
    }

    // 🧠 ADMIN — Get all orders
    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    @PostMapping("/payment/create")
    public ResponseEntity<String> createPayment(@RequestParam int amount) {
        return ResponseEntity.ok(razorpayService.createOrder(amount));
    }
    @PostMapping("/{orderId}/payment")
    public ResponseEntity<String> createPayment(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        int amount = order.getTotalAmount().intValue();
        return ResponseEntity.ok(razorpayService.createOrder(amount));
    }
}