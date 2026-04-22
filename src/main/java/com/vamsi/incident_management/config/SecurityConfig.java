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

                // 🔥 AUTHORIZATION RULES
                .authorizeHttpRequests(auth -> auth

                        // ✅ Public (login + static)
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/pages/login.html",
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

                        // 🔥 PROTECT ALL PAGES
                        .requestMatchers("/pages/**").permitAll()

                        // 🔥 PROTECT INCIDENT APIs
                        .requestMatchers("/api/incidents/**").authenticated()

                        // 🔥 Allow preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔒 Everything else
                        .anyRequest().authenticated()
                )

                // 🔹 JWT Filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔹 Required for login
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}