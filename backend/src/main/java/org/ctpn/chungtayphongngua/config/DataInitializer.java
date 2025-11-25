package org.ctpn.chungtayphongngua.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.ctpn.chungtayphongngua.entity.User;
import org.ctpn.chungtayphongngua.repository.UserRepository;

@Configuration
public class DataInitializer {

    // @Bean
    // CommandLineRunner initTestUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    //     return args -> {
    //         String email = "hardcoded@example.com";
    //         if (!userRepository.existsByEmailAndIsDeletedFalse(email)) {
    //             User user = new User();
    //             user.setEmail(email);
    //             user.setFullName("Hardcoded User");
    //             user.setPasswordHash(passwordEncoder.encode("123456"));
    //             user.setIsVerified(true);
    //             user.setIsDeleted(false);
    //             user.setPhone("0900000000");
    //             user.setDateOfBirth(java.time.LocalDateTime.of(2000, 1, 1, 0, 0));
    //             userRepository.save(user);
    //             System.out.println("Hardcoded test user created: " + email);
    //         }
    //     };
    // }
} 