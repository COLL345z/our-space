package com.example.demo.repository;

import com.example.demo.entity.SpecialDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialDayRepository extends JpaRepository<SpecialDay, Long> {
}