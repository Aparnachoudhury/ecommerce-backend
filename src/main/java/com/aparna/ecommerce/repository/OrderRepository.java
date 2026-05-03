package com.aparna.ecommerce.repository;

import com.aparna.ecommerce.entity.Order;
import com.aparna.ecommerce.entity.OrderStatus;
import com.aparna.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_Id(Long userId);

    List<Order> findByUser(User user);
    List<Order> findByUserEmail(String email);

    List<Order> findByStatus(OrderStatus status);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);
}