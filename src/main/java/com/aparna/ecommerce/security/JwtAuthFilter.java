package com.aparna.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // ✅ Public endpoints (no JWT required)
    private static final List<String> PUBLIC_URLS = List.of(
            "/api/auth",
            "/signup"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ✅ STEP 1: Get request path (FIXED)
        String path = request.getRequestURI();

        // ✅ STEP 2: Skip public endpoints
        boolean isPublic = PUBLIC_URLS.stream()
                .anyMatch(path::startsWith);

        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ STEP 3: Get Authorization header
        final String authHeader = request.getHeader("Authorization");

        // If no token → continue
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ STEP 4: Extract token
        final String jwt = authHeader.substring(7);

        // Extract email from token
        final String userEmail = jwtService.extractEmail(jwt);

        // ✅ STEP 5: Validate token
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ✅ STEP 6: Continue filter chain
        filterChain.doFilter(request, response);
    }
}