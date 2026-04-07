package com.aparna.ecommerce.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.core.annotation.Order;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    // Separate bucket map per IP per endpoint
    private final Map<String, Bucket> loginBuckets =
            new ConcurrentHashMap<>();
    private final Map<String, Bucket> signupBuckets =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("🔥 RateLimitFilter hit: " + request.getRequestURI());

        String path = request.getRequestURI();
        if (path.contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        String ip = getClientIp(request);

        if (path.equals("/api/auth/login")) {
            Bucket bucket = loginBuckets.computeIfAbsent(
                    ip, k -> buildLoginBucket());
            if (!bucket.tryConsume(1)) {
                sendRateLimitError(response);
                return;
            }
        }

        if (path.equals("/api/auth/signup")) {
            Bucket bucket = signupBuckets.computeIfAbsent(
                    ip, k -> buildSignupBucket());
            if (!bucket.tryConsume(1)) {
                sendRateLimitError(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    // 10 requests per 15 minutes per IP
    private Bucket buildLoginBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillIntervally(10, Duration.ofMinutes(15))
                        .build())
                .build();
    }

    // 5 requests per 15 minutes per IP
    private Bucket buildSignupBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(5)
                        .refillIntervally(5, Duration.ofMinutes(15))
                        .build())
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
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