package com.aparna.ecommerce.controller;

import com.aparna.ecommerce.dto.CartItem;
import com.aparna.ecommerce.dto.CartRequest;
import com.aparna.ecommerce.service.CartService;
import com.aparna.ecommerce.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final WishlistService wishlistService;

    @PostMapping("/add")
    public ResponseEntity<List<CartItem>> addItem(
            @RequestBody CartRequest request,
            @RequestHeader(value = "X-Guest-Id",
                    required = false) String guestId) {
        return ResponseEntity.ok(
                cartService.addItem(request, guestId));
    }

    @DeleteMapping("/remove/{variantId}")
    public ResponseEntity<List<CartItem>> removeItem(
            @PathVariable Long variantId,
            @RequestHeader(value = "X-Guest-Id",
                    required = false) String guestId) {
        return ResponseEntity.ok(
                cartService.removeItem(variantId, guestId));
    }

    @PutMapping("/update")
    public ResponseEntity<List<CartItem>> updateItem(
            @RequestBody CartRequest request,
            @RequestHeader(value = "X-Guest-Id",
                    required = false) String guestId) {
        return ResponseEntity.ok(
                cartService.updateItem(request, guestId));
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(
            @RequestHeader(value = "X-Guest-Id",
                    required = false) String guestId) {
        return ResponseEntity.ok(
                cartService.getCartItems(guestId));
    }

    @PostMapping("/merge")
    public ResponseEntity<List<CartItem>> mergeCart(
            @RequestHeader(value = "X-Guest-Id",
                    required = false) String guestId) {
        return ResponseEntity.ok(
                cartService.mergeGuestCart(guestId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            @RequestHeader(value = "X-Guest-Id",
                    required = false) String guestId) {
        cartService.clearCart(guestId);
        return ResponseEntity.noContent().build();
    }

    // Wishlist endpoints
    @PostMapping("/wishlist/{productId}")
    public ResponseEntity<Set<Long>> addWishlist(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
                wishlistService.addToWishlist(productId));
    }

    @DeleteMapping("/wishlist/{productId}")
    public ResponseEntity<Set<Long>> removeWishlist(
            @PathVariable Long productId) {
        return ResponseEntity.ok(
                wishlistService.removeFromWishlist(productId));
    }

    @GetMapping("/wishlist")
    public ResponseEntity<Set<Long>> getWishlist() {
        return ResponseEntity.ok(
                wishlistService.getWishlist());
    }
}