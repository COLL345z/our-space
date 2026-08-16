package com.example.demo.repository;

import com.example.demo.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findByCategory(String category);
    List<Memory> findByCategoryOrderByDateCreatedDesc(String category);
}
