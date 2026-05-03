package com.aparna.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WishlistService {

    // 🔥 In-memory wishlist (NO REDIS)
    private final Map<String, Set<Long>> wishlistStore = new HashMap<>();

    private String getWishlistKey() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return "wishlist:" + email;
    }

    // ➕ Add to wishlist
    public Set<Long> addToWishlist(Long productId) {
        String key = getWishlistKey();
        Set<Long> wishlist = wishlistStore.getOrDefault(key, new HashSet<>());

        wishlist.add(productId);
        wishlistStore.put(key, wishlist);

        return wishlist;
    }

    // ❌ Remove from wishlist
    public Set<Long> removeFromWishlist(Long productId) {
        String key = getWishlistKey();
        Set<Long> wishlist = wishlistStore.getOrDefault(key, new HashSet<>());

        wishlist.remove(productId);
        return wishlist;
    }

    // 📦 Get wishlist
    public Set<Long> getWishlist() {
        return wishlistStore.getOrDefault(getWishlistKey(), new HashSet<>());
    }
}