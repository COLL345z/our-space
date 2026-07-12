package com.example.demo.repository;

import com.example.demo.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByStatus(String status);
    List<Movie> findByGenre(String genre);
    long countByStatus(String status);
}
