package com.aparna.ecommerce.service;

import com.aparna.ecommerce.dto.CartItem;
import com.aparna.ecommerce.dto.CartRequest;
import com.aparna.ecommerce.entity.ProductVariant;
import com.aparna.ecommerce.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final RedissonClient redissonClient;
    private final ProductVariantRepository variantRepository;

    @Value("${app.cart.guest-ttl-days}")
    private long guestTtlDays;

    @Value("${app.cart.user-ttl-days}")
    private long userTtlDays;

    // ── ADD ITEM ──────────────────────────────────────────────────
    public List<CartItem> addItem(CartRequest request,
                                  String guestId) {
        String cartKey = getCartKey(guestId);
        RMap<String, CartItem> cart =
                redissonClient.getMap(cartKey);

        ProductVariant variant = variantRepository
                .findById(request.getVariantId())
                .orElseThrow(() ->
                        new RuntimeException("Variant not found"));

        String itemKey = buildItemKey(
                variant.getProduct().getId(),
                variant.getId());

        CartItem existing = cart.get(itemKey);
        if (existing != null) {
            existing.setQuantity(
                    existing.getQuantity() + request.getQuantity());
            existing.setTotalPrice(
                    variant.getPrice().multiply(
                            BigDecimal.valueOf(existing.getQuantity())));
            cart.put(itemKey, existing);
        } else {
            CartItem item = new CartItem(
                    variant.getProduct().getId(),
                    variant.getId(),
                    variant.getProduct().getName(),
                    variant.getName(),
                    variant.getSku(),
                    request.getQuantity(),
                    variant.getPrice(),
                    variant.getPrice().multiply(
                            BigDecimal.valueOf(request.getQuantity()))
            );
            cart.put(itemKey, item);
        }

        setTtl(cart, cartKey, guestId);
        log.info("Item added to cart: {}", cartKey);
        return getCartItems(guestId);
    }

    // ── REMOVE ITEM ───────────────────────────────────────────────
    public List<CartItem> removeItem(Long variantId,
                                     String guestId) {
        String cartKey = getCartKey(guestId);
        RMap<String, CartItem> cart =
                redissonClient.getMap(cartKey);

        ProductVariant variant = variantRepository
                .findById(variantId)
                .orElseThrow(() ->
                        new RuntimeException("Variant not found"));

        String itemKey = buildItemKey(
                variant.getProduct().getId(), variantId);
        cart.remove(itemKey);

        log.info("Item removed from cart: {}", cartKey);
        return getCartItems(guestId);
    }

    // ── UPDATE QUANTITY ───────────────────────────────────────────
    public List<CartItem> updateItem(CartRequest request,
                                     String guestId) {
        String cartKey = getCartKey(guestId);
        RMap<String, CartItem> cart =
                redissonClient.getMap(cartKey);

        ProductVariant variant = variantRepository
                .findById(request.getVariantId())
                .orElseThrow(() ->
                        new RuntimeException("Variant not found"));

        String itemKey = buildItemKey(
                variant.getProduct().getId(),
                variant.getId());

        CartItem item = cart.get(itemKey);
        if (item == null) {
            throw new RuntimeException("Item not in cart");
        }

        if (request.getQuantity() <= 0) {
            cart.remove(itemKey);
        } else {
            item.setQuantity(request.getQuantity());
            item.setTotalPrice(variant.getPrice().multiply(
                    BigDecimal.valueOf(request.getQuantity())));
            cart.put(itemKey, item);
        }

        return getCartItems(guestId);
    }

    // ── GET CART ──────────────────────────────────────────────────
    public List<CartItem> getCartItems(String guestId) {
        String cartKey = getCartKey(guestId);
        RMap<String, CartItem> cart =
                redissonClient.getMap(cartKey);
        return new ArrayList<>(cart.values());
    }

    // ── CLEAR CART ────────────────────────────────────────────────
    public void clearCart(String guestId) {
        String cartKey = getCartKey(guestId);
        redissonClient.getMap(cartKey).delete();
        log.info("Cart cleared: {}", cartKey);
    }

    // ── MERGE GUEST CART ON LOGIN ─────────────────────────────────
    public List<CartItem> mergeGuestCart(String guestId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        String userCartKey = "cart:user:" + email;
        String guestCartKey = "cart:guest:" + guestId;

        RMap<String, CartItem> guestCart =
                redissonClient.getMap(guestCartKey);
        RMap<String, CartItem> userCart =
                redissonClient.getMap(userCartKey);

        for (Map.Entry<String, CartItem> entry :
                guestCart.entrySet()) {
            CartItem guestItem = entry.getValue();
            CartItem existing = userCart.get(entry.getKey());

            if (existing != null) {
                existing.setQuantity(
                        existing.getQuantity() +
                                guestItem.getQuantity());
                existing.setTotalPrice(
                        guestItem.getUnitPrice().multiply(
                                BigDecimal.valueOf(
                                        existing.getQuantity())));
                userCart.put(entry.getKey(), existing);
            } else {
                userCart.put(entry.getKey(), guestItem);
            }
        }

        // Delete guest cart after merge
        guestCart.delete();
        userCart.expire(java.time.Duration.ofDays(userTtlDays));

        log.info("Guest cart merged into user cart: {}", email);
        return new ArrayList<>(userCart.values());
    }

    // ── HELPERS ───────────────────────────────────────────────────
    private String getCartKey(String guestId) {
        String auth = null;
        try {
            auth = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
        } catch (Exception ignored) {}

        if (auth != null && !auth.equals("anonymousUser")) {
            return "cart:user:" + auth;
        }
        return "cart:guest:" + guestId;
    }

    private void setTtl(RMap<String, CartItem> cart,
                        String cartKey, String guestId) {
        if (cartKey.startsWith("cart:guest:")) {
            cart.expire(java.time.Duration.ofDays(guestTtlDays));

        } else {
            cart.expire(java.time.Duration.ofDays(userTtlDays));
        }
    }

    private String buildItemKey(Long productId, Long variantId) {
        return productId + ":" + variantId;
    }
}