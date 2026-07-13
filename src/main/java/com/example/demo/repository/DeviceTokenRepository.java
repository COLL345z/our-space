package com.example.demo.repository;

import com.example.demo.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findByUsername(String username);
    Optional<DeviceToken> findByToken(String token);
}
