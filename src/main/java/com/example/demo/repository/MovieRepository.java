package com.example.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // Find movies by status
    List<Movie> findByStatus(String status);

    // Find movies by genre
    List<Movie> findByGenre(String genre);

    // Find movies by status and genre
    List<Movie> findByStatusAndGenre(String status, String genre);

    // Search movies by title (case-insensitive)
    List<Movie> findByTitleContainingIgnoreCase(String title);

    // Get all movies ordered by date added (newest first)
    List<Movie> findAllByOrderByCreatedAtDesc();

    // Count movies by status
    long countByStatus(String status);
}
