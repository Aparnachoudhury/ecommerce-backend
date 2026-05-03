package com.aparna.ecommerce.service;

import com.aparna.ecommerce.dto.CartItem;
import com.aparna.ecommerce.dto.CartRequest;
import com.aparna.ecommerce.entity.ProductVariant;
import com.aparna.ecommerce.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductVariantRepository variantRepository;

    // 🔥 In-memory cart (NO REDIS)
    private final Map<String, List<CartItem>> cartStore = new HashMap<>();

    // ── ADD ITEM ─────────────────────────────
    public List<CartItem> addItem(CartRequest request, String guestId) {
        String key = getCartKey(guestId);

        ProductVariant variant = variantRepository
                .findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        List<CartItem> cart = cartStore.getOrDefault(key, new ArrayList<>());

        Optional<CartItem> existing = cart.stream()
                .filter(i -> i.getVariantId().equals(variant.getId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setTotalPrice(
                    variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        } else {
            CartItem item = new CartItem(
                    variant.getProduct().getId(),
                    variant.getId(),
                    variant.getProduct().getName(),
                    variant.getName(),
                    variant.getSku(),
                    request.getQuantity(),
                    variant.getPrice(),
                    variant.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()))
            );
            cart.add(item);
        }

        cartStore.put(key, cart);
        return cart;
    }

    // ── GET CART ─────────────────────────────
    public List<CartItem> getCartItems(String guestId) {
        return cartStore.getOrDefault(getCartKey(guestId), new ArrayList<>());
    }

    // ── CLEAR CART ───────────────────────────
    public void clearCart(String guestId) {
        cartStore.remove(getCartKey(guestId));
    }

    // ── REMOVE ITEM ──────────────────────────
    public List<CartItem> removeItem(Long variantId, String guestId) {
        String key = getCartKey(guestId);
        List<CartItem> cart = cartStore.getOrDefault(key, new ArrayList<>());

        cart.removeIf(item -> item.getVariantId().equals(variantId));
        return cart;
    }

    // ── UPDATE ITEM ──────────────────────────
    public List<CartItem> updateItem(CartRequest request, String guestId) {
        String key = getCartKey(guestId);
        List<CartItem> cart = cartStore.getOrDefault(key, new ArrayList<>());

        for (CartItem item : cart) {
            if (item.getVariantId().equals(request.getVariantId())) {
                item.setQuantity(request.getQuantity());
                item.setTotalPrice(
                        item.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()))
                );
            }
        }
        return cart;
    }

    // ── KEY ──────────────────────────────────
    private String getCartKey(String guestId) {
        String user;
        try {
            user = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            user = null;
        }

        if (user != null && !user.equals("anonymousUser")) {
            return "user:" + user;
        }
        return "guest:" + guestId;
    }
}