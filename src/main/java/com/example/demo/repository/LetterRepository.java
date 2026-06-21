package com.example.demo.repository;

import com.example.demo.entity.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {

     // Existing
    List<Letter> findByReceiver(String receiver);
    List<Letter> findBySender(String sender);

    // New: only sent letters
    List<Letter> findByReceiverAndStatus(String receiver, String status);
    List<Letter> findBySenderAndStatus(String sender, String status);
    
    
    // in LetterRepository.java
List<Letter> findByParentId(Long parentId);
}