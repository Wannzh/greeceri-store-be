package com.greeceri.store.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.enums.Role;
import com.greeceri.store.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@Slf4j
public class DataInitializer{

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.username}")
    private String adminUsername;

    @Bean
    CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User adminUser = User.builder()
                        .name(adminUsername)
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();
                userRepository.save(adminUser);
                log.info("Admin user created with email: {}", adminEmail);
            } else {
                log.info("Admin user already exists with email: {}", adminEmail);
            }
        };
        
    }


}
