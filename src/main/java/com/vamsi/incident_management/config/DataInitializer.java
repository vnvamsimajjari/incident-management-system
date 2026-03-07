package com.vamsi.incident_management.config;

import com.vamsi.incident_management.entity.Role;
import com.vamsi.incident_management.entity.User;
import com.vamsi.incident_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {

            userRepository.save(
                    User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin123"))
                            .role(Role.ADMIN)
                            .enabled(true)
                            .build()
            );

            userRepository.save(
                    User.builder()
                            .username("engineer")
                            .password(passwordEncoder.encode("engineer123"))
                            .role(Role.ENGINEER)
                            .enabled(true)
                            .build()
            );
        }
    }
}