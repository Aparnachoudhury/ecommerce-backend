package com.aparna.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final RedissonClient redissonClient;

    private String getWishlistKey() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return "wishlist:" + email;
    }

    public Set<Long> addToWishlist(Long productId) {
        RSet<Long> wishlist =
                redissonClient.getSet(getWishlistKey());
        wishlist.add(productId);
        log.info("Added {} to wishlist", productId);
        return wishlist.readAll();
    }

    public Set<Long> removeFromWishlist(Long productId) {
        RSet<Long> wishlist =
                redissonClient.getSet(getWishlistKey());
        wishlist.remove(productId);
        return wishlist.readAll();
    }

    public Set<Long> getWishlist() {
        RSet<Long> wishlist =
                redissonClient.getSet(getWishlistKey());
        return wishlist.readAll();
    }

    public void clearWishlist() {
        redissonClient.getSet(getWishlistKey()).delete();
    }
}