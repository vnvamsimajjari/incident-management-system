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
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ✅ Static resources
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/pages/**",
                                "/css/**",
                                "/js/**",
                                "/assets/**",
                                "/favicon.ico"
                        ).permitAll()

                        // ✅ Auth APIs
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register"
                        ).permitAll()

                        // ✅ Allow preflight (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 🔒 Incident APIs
                        .requestMatchers(HttpMethod.GET, "/api/incidents/**")
                        .hasAnyRole("ADMIN", "ENGINEER")

                        .requestMatchers(HttpMethod.POST, "/api/incidents/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/incidents/**")
                        .hasAnyRole("ADMIN", "ENGINEER")

                        .requestMatchers(HttpMethod.DELETE, "/api/incidents/**")
                        .hasRole("ADMIN")

                        // 🔒 Everything else
                        .anyRequest().authenticated()
                )

                // ✅ JWT Filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}