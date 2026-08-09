package com.example.demo.repository;

import com.example.demo.entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    // Find by game
    List<Round> findByGameId(Long gameId);
    
    // Find by status
    List<Round> findByStatus(String status);
    
    // Find by game and status
    List<Round> findByGameIdAndStatus(Long gameId, String status);
    
    // Find by date range
    List<Round> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Find latest rounds for a game
    List<Round> findTop5ByGameIdOrderByCreatedAtDesc(Long gameId);
}
