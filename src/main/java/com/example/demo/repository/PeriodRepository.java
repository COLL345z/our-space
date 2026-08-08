package com.example.demo.repository;

import com.example.demo.entity.PeriodEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository extends JpaRepository<PeriodEntry, Long> {}
