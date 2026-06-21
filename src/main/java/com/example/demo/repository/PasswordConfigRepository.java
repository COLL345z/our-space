package com.example.demo.repository;

import com.example.demo.entity.PasswordConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordConfigRepository extends JpaRepository<PasswordConfig, Long> {
}