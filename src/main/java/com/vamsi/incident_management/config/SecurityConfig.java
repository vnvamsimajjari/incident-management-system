package com.vamsi.incident_management.config;

import com.vamsi.incident_management.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 🔹 Disable CSRF
                .csrf(csrf -> csrf.disable())

                // 🔹 Disable default login
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 🔹 Stateless session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔥 Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // ✅ Static frontend files
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/pages/**",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ✅ Auth APIs
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register"
                        ).permitAll()

                        // 🔥 DEMO: allow all incident APIs
                        .requestMatchers("/api/incidents/**").permitAll()

                        // 🔥 Allow preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔥 Allow everything else (for demo)
                        .anyRequest().permitAll()
                )

                // 🔹 JWT filter (kept, not mandatory for demo)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔥 FIX: Required for AuthController (IMPORTANT)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}