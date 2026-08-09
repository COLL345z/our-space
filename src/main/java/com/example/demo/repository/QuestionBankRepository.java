package com.example.demo.repository;

import com.example.demo.entity.QuestionBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {
    List<QuestionBank> findByMode(String mode);
    
    // Additional useful methods
    List<QuestionBank> findByModeOrderByIdAsc(String mode);
    List<QuestionBank> findByCategory(String category);
    List<QuestionBank> findByModeAndCategory(String mode, String category);
    long countByMode(String mode);
}
