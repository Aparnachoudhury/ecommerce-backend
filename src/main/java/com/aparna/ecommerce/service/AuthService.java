package com.aparna.ecommerce.service;

import com.aparna.ecommerce.dto.AuthResponse;
import com.aparna.ecommerce.dto.LoginRequest;
import com.aparna.ecommerce.dto.SignupRequest;
import com.aparna.ecommerce.entity.RefreshToken;
import com.aparna.ecommerce.entity.RoleType;
import com.aparna.ecommerce.entity.User;
import com.aparna.ecommerce.entity.VerificationToken;
import com.aparna.ecommerce.repository.RefreshTokenRepository;
import com.aparna.ecommerce.repository.UserRepository;
import com.aparna.ecommerce.repository.VerificationTokenRepository;
import com.aparna.ecommerce.security.CustomUserDetailsService;
import com.aparna.ecommerce.security.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    // ─── SIGNUP ───────────────────────────────────────────────────
    public AuthResponse signup(SignupRequest request,
                               HttpServletResponse response) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleType.CUSTOMER);
        user.setEmailVerified(false);
        user.setEnabled(true);

        userRepository.save(user);

        // verification token
        String verifyToken = UUID.randomUUID().toString();

        VerificationToken token = new VerificationToken();
        token.setToken(verifyToken);
        token.setUser(user);

        // ✅ FIXED (no ChronoUnit)
        token.setExpiryDate(
                Instant.now().plusSeconds(60 * 60 * 24) // 1 day
        );

        verificationTokenRepository.save(token);

        log.info("=== EMAIL VERIFY LINK (DEV) ===");
        log.info("http://localhost:8080/api/auth/verify-email?token={}", verifyToken);
        log.info("================================");

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtService.generateAccessToken(userDetails);

        createAndSetRefreshCookie(user, response);

        return new AuthResponse(
                accessToken,
                user.getEmail(),
                user.getRole().name()
        );
    }

    // ─── LOGIN ────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request,
                              HttpServletResponse response) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(),
                user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtService.generateAccessToken(userDetails);

        createAndSetRefreshCookie(user, response);

        return new AuthResponse(
                accessToken,
                user.getEmail(),
                user.getRole().name()
        );
    }

    // ─── REFRESH ──────────────────────────────────────────────────
    @Transactional
    public AuthResponse refresh(HttpServletRequest request,
                                HttpServletResponse response) {

        String token = extractRefreshTokenFromCookie(request)
                .orElseThrow(() ->
                        new RuntimeException("Refresh token not found"));

        RefreshToken saved = refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new RuntimeException("Invalid refresh token"));

        if (saved.isRevoked()) {
            refreshTokenRepository.deleteByUser(saved.getUser());
            throw new RuntimeException("Token reuse detected");
        }

        if (saved.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        refreshTokenRepository.delete(saved);

        User user = saved.getUser();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtService.generateAccessToken(userDetails);

        createAndSetRefreshCookie(user, response);

        return new AuthResponse(
                accessToken,
                user.getEmail(),
                user.getRole().name()
        );
    }

    // ─── LOGOUT ───────────────────────────────────────────────────
    @Transactional
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {

        extractRefreshTokenFromCookie(request).ifPresent(token ->
                refreshTokenRepository.findByToken(token)
                        .ifPresent(refreshTokenRepository::delete)
        );

        clearRefreshCookie(response);
    }

    public void verifyEmail(String token) {
        log.info("Email verified with token: {}", token);
    }

    // ─── HELPERS ──────────────────────────────────────────────────
    private void createAndSetRefreshCookie(User user,
                                           HttpServletResponse response) {

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(
                Instant.now().plusMillis(refreshTokenExpiry));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        Cookie cookie = new Cookie("refreshToken",
                refreshToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge((int) (refreshTokenExpiry / 1000));

        response.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private Optional<String> extractRefreshTokenFromCookie(
            HttpServletRequest request) {

        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}