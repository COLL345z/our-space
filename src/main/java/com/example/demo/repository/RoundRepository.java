package com.example.demo.repository;

import com.example.demo.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {}
