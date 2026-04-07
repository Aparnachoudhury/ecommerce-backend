package com.aparna.ecommerce.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // 🔐 Your JWT filter
    private final CustomUserDetailsService userDetailsService;

    // 🔑 Password encoder (secure)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16, 32, 1, 65536, 3);
    }

    // 👥 Role hierarchy (optional but useful)
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("""
                ROLE_ADMIN > ROLE_VENDOR
                ROLE_VENDOR > ROLE_CUSTOMER
                """);
        return hierarchy;
    }

    // 🔐 Authentication provider (uses your custom user service)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // 🔑 Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 🚀 MAIN SECURITY CONFIG (MOST IMPORTANT PART)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ❌ Disable CSRF (important for Postman / APIs)
                .csrf(csrf -> csrf.disable())

                // 🔥 Make API stateless (no sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔐 Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC ENDPOINTS (VERY IMPORTANT)
                        // 👉 MAKE SURE THIS MATCHES YOUR CONTROLLER EXACTLY
                        .requestMatchers(
                                "/signup",
                                "/api/auth/signup",
                                "/api/auth/login"
                        ).permitAll()

                        // 🔒 Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // 🔐 Set authentication provider
                .authenticationProvider(authenticationProvider())

                // 🔥 ADD JWT FILTER (CRITICAL)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}