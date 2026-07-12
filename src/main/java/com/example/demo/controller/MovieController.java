package com.example.demo.controller;

import com.example.demo.entity.Movie;
import com.example.demo.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    // ─── GET all movies ─────────────────────────────────────────
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // ─── GET movie by id ────────────────────────────────────────
    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id).orElse(null);
    }

    // ─── CREATE movie ───────────────────────────────────────────
    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        // Set defaults
        if (movie.getStatus() == null || movie.getStatus().isEmpty()) {
            movie.setStatus("WATCHLIST");
        }
        if (movie.getDateAdded() == null || movie.getDateAdded().isEmpty()) {
            movie.setDateAdded(LocalDate.now().toString());
        }
        if (movie.getGenre() == null || movie.getGenre().isEmpty()) {
            movie.setGenre("MOVIE");
        }
        if (movie.getDescription() == null) {
            movie.setDescription("");
        }
        if (movie.getYear() == null) {
            movie.setYear("");
        }

        // Validate ratings
        if (movie.getRatingRehema() < 0 || movie.getRatingRehema() > 5) {
            movie.setRatingRehema(0);
        }
        if (movie.getRatingCollins() < 0 || movie.getRatingCollins() > 5) {
            movie.setRatingCollins(0);
        }

        return movieRepository.save(movie);
    }

    // ─── UPDATE movie ───────────────────────────────────────────
    @PutMapping("/{id}")
    public Movie updateMovie(@PathVariable Long id, @RequestBody Movie updated) {
        return movieRepository.findById(id).map(movie -> {
            movie.setTitle(updated.getTitle());
            movie.setDescription(updated.getDescription());
            movie.setGenre(updated.getGenre());
            movie.setYear(updated.getYear());
            movie.setStatus(updated.getStatus());
            movie.setDateAdded(updated.getDateAdded());
            movie.setDateWatched(updated.getDateWatched());
            movie.setRatingRehema(updated.getRatingRehema());
            movie.setRatingCollins(updated.getRatingCollins());
            return movieRepository.save(movie);
        }).orElse(null);
    }

    // ─── UPDATE movie status ────────────────────────────────────
    @PutMapping("/{id}/status")
    public Movie updateMovieStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return movieRepository.findById(id).map(movie -> {
            String newStatus = body.get("status");
            if (newStatus != null) {
                movie.setStatus(newStatus);

                // If marked as watched, set the date
                if ("WATCHED".equals(newStatus)) {
                    String dateWatched = body.getOrDefault("dateWatched", LocalDate.now().toString());
                    movie.setDateWatched(dateWatched);
                }
            }
            return movieRepository.save(movie);
        }).orElse(null);
    }

    // ─── UPDATE movie rating ────────────────────────────────────
    @PutMapping("/{id}/rating")
    public Movie updateMovieRating(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return movieRepository.findById(id).map(movie -> {
            // Update Rehema's rating
            if (body.containsKey("ratingRehema")) {
                int rating = ((Number) body.get("ratingRehema")).intValue();
                if (rating >= 0 && rating <= 5) {
                    movie.setRatingRehema(rating);
                }
            }
            // Update Collins' rating
            if (body.containsKey("ratingCollins")) {
                int rating = ((Number) body.get("ratingCollins")).intValue();
                if (rating >= 0 && rating <= 5) {
                    movie.setRatingCollins(rating);
                }
            }
            return movieRepository.save(movie);
        }).orElse(null);
    }

    // ─── DELETE movie ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }

    // ─── GET by status ──────────────────────────────────────────
    @GetMapping("/status/{status}")
    public List<Movie> getByStatus(@PathVariable String status) {
        return movieRepository.findByStatus(status);
    }

    // ─── GET by genre ───────────────────────────────────────────
    @GetMapping("/genre/{genre}")
    public List<Movie> getByGenre(@PathVariable String genre) {
        return movieRepository.findByGenre(genre);
    }
}
