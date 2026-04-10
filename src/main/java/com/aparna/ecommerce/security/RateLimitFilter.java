package com.aparna.ecommerce.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> loginBuckets =
            new ConcurrentHashMap<>();
    private final Map<String, Bucket> signupBuckets =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = getClientIp(request);

        // Rate limit login
        if (path.equals("/api/auth/login")) {
            Bucket bucket = loginBuckets.computeIfAbsent(
                    ip, k -> buildLoginBucket());
            if (!bucket.tryConsume(1)) {
                sendRateLimitError(response);
                return;
            }
        }

        // Rate limit signup
        if (path.equals("/api/auth/signup")) {
            Bucket bucket = signupBuckets.computeIfAbsent(
                    ip, k -> buildSignupBucket());
            if (!bucket.tryConsume(1)) {
                sendRateLimitError(response);
                return;
            }
        }

        // All other requests pass through
        filterChain.doFilter(request, response);
    }

    private Bucket buildLoginBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillIntervally(10,
                                Duration.ofMinutes(15))
                        .build())
                .build();
    }

    private Bucket buildSignupBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(5)
                        .refillIntervally(5,
                                Duration.ofMinutes(15))
                        .build())
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded =
                request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendRateLimitError(HttpServletResponse response)
            throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("""
                {
                  "error": "Too many requests",
                  "message": "Please try again later"
                }
                """);
    }
}