package com.aparna.ecommerce.service;

import com.aparna.ecommerce.dto.CartItem;
import com.aparna.ecommerce.entity.*;
import com.aparna.ecommerce.repository.OrderRepository;
import com.aparna.ecommerce.repository.ProductVariantRepository;
import com.aparna.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CartService cartService;
    private final SmsService smsService;
    private final ProductVariantRepository productVariantRepository;


    // 🛒 CREATE ORDER
    @Transactional
    public Order createOrder(String guestId, String shippingAddress) {

        // ✅ Get logged-in user
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Get cart items
        List<CartItem> cartItems = cartService.getCartItems(guestId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        String paymentId = "PAY_" + System.currentTimeMillis();

        // ✅ Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingAddress(shippingAddress);
        order.setPaymentIntentId(paymentId);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            ProductVariant variant = productVariantRepository.findById(cartItem.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));

            if (variant.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Out of stock");
            }

            // ✅ Reduce stock
            variant.setStock(variant.getStock() - cartItem.getQuantity());
            productVariantRepository.save(variant);

            BigDecimal unitPrice = variant.getPrice();
            BigDecimal totalPrice = unitPrice.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())
            );

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setVariant(variant);
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(totalPrice);

            orderItems.add(item);
            totalAmount = totalAmount.add(totalPrice);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // ✅ SAVE ORDER FIRST
        Order savedOrder = orderRepository.save(order);

        // ✅ CLEAR CART
        cartService.clearCart(guestId);

        // ✅ SEND EMAIL
        emailService.sendEmail(
                user.getEmail(),
                "Order Confirmed",
                "<h2>Order Confirmed ✅</h2>" +
                        "<p>Your order <b>#"+ savedOrder.getId() +"</b> is placed successfully.</p>"
        );

        // ✅ SEND SMS (FIXED HERE)
        smsService.sendSms(
                "+918458039547",
                "Your order #" + savedOrder.getId() + " is placed successfully!"
        );

        return savedOrder;
    }

    // 📦 GET MY ORDERS
    public List<Order> getMyOrders() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser_Id(user.getId());
    }

    // 📦 GET ORDER BY ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // 💰 MARK ORDER AS PAID
    @Transactional
    public Order markAsPaid(Long id) {
        Order order = getOrderById(id);

        order.setStatus(OrderStatus.PAID);

        Order updated = orderRepository.save(order);

        messagingTemplate.convertAndSend(
                "/topic/orders",
                "Order " + id + " marked as PAID"
        );

        return updated;
    }

    public List<Order> getOrdersByUser(String email) {
        return orderRepository.findByUserEmail(email);
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 🔄 UPDATE ORDER STATUS
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);

        Order updated = orderRepository.save(order);

        messagingTemplate.convertAndSend(
                "/topic/orders",
                "Order " + id + " updated to " + status
        );

        return updated;
    }
}