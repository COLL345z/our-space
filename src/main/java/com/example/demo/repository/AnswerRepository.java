package com.example.demo.repository;

import com.example.demo.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByRoundId(Long roundId);
    
    // Additional useful methods
    List<Answer> findByRoundIdOrderByCreatedAtAsc(Long roundId);
    
    List<Answer> findByRoundIdAndQuestionId(Long roundId, Long questionId);
    
    List<Answer> findByPlayerId(Long playerId);
    
    List<Answer> findByRoundIdAndIsCorrect(Long roundId, boolean isCorrect);
    
    long countByRoundIdAndIsCorrect(Long roundId, boolean isCorrect);
}
