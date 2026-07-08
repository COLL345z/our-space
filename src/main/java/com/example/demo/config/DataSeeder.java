package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedUsers(UserRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                User rehema = new User();
                rehema.setUsername("Rehema");
                rehema.setPasswordHash(encoder.encode("rehema123")); // change via app after first login
                repo.save(rehema);

                User collins = new User();
                collins.setUsername("Collins");
                collins.setPasswordHash(encoder.encode("collins123"));
                repo.save(collins);
            }
        };
    }
}
