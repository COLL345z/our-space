package com.example.demo.controller;

import com.example.demo.repository.MovieRepository;
import com.example.demo.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")  // Allow requests from your Android app
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    // ─── GET ALL MOVIES ──────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(movies);
    }

    // ─── GET MOVIE BY ID ────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + id));
        return ResponseEntity.ok(movie);
    }

    // ─── GET MOVIES BY STATUS ───────────────────────────────────
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Movie>> getMoviesByStatus(@PathVariable String status) {
        List<Movie> movies = movieRepository.findByStatus(status.toUpperCase());
        return ResponseEntity.ok(movies);
    }

    // ─── ADD MOVIE ──────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        // Set default values if not provided
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

        Movie savedMovie = movieRepository.save(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    // ─── UPDATE MOVIE ───────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movieDetails) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + id));

        // Update only the fields that are provided
        if (movieDetails.getTitle() != null) {
            movie.setTitle(movieDetails.getTitle());
        }
        if (movieDetails.getDescription() != null) {
            movie.setDescription(movieDetails.getDescription());
        }
        if (movieDetails.getGenre() != null) {
            movie.setGenre(movieDetails.getGenre());
        }
        if (movieDetails.getYear() != null) {
            movie.setYear(movieDetails.getYear());
        }
        if (movieDetails.getStatus() != null) {
            movie.setStatus(movieDetails.getStatus());
        }
        if (movieDetails.getDateAdded() != null) {
            movie.setDateAdded(movieDetails.getDateAdded());
        }
        if (movieDetails.getDateWatched() != null) {
            movie.setDateWatched(movieDetails.getDateWatched());
        }
        movie.setRatingRehema(movieDetails.getRatingRehema());
        movie.setRatingCollins(movieDetails.getRatingCollins());

        Movie updatedMovie = movieRepository.save(movie);
        return ResponseEntity.ok(updatedMovie);
    }

    // ─── UPDATE MOVIE STATUS ────────────────────────────────────
    @PutMapping("/{id}/status")
    public ResponseEntity<Movie> updateMovieStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + id));

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        // Validate status
        if (!newStatus.equals("WATCHLIST") && !newStatus.equals("WATCHING") && !newStatus.equals("WATCHED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status. Must be WATCHLIST, WATCHING, or WATCHED");
        }

        movie.setStatus(newStatus);

        // If marking as watched, set the date
        if (newStatus.equals("WATCHED")) {
            String dateWatched = body.getOrDefault("dateWatched", LocalDate.now().toString());
            movie.setDateWatched(dateWatched);
        }

        Movie updatedMovie = movieRepository.save(movie);
        return ResponseEntity.ok(updatedMovie);
    }

    // ─── UPDATE MOVIE RATING ────────────────────────────────────
    @PutMapping("/{id}/rating")
    public ResponseEntity<Movie> updateMovieRating(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + id));

        // Update Rehema's rating
        if (body.containsKey("ratingRehema")) {
            Object ratingObj = body.get("ratingRehema");
            int rating = ratingObj instanceof Integer ? (Integer) ratingObj : ((Number) ratingObj).intValue();
            
            if (rating < 0 || rating > 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5");
            }
            movie.setRatingRehema(rating);
        }

        // Update Collins' rating
        if (body.containsKey("ratingCollins")) {
            Object ratingObj = body.get("ratingCollins");
            int rating = ratingObj instanceof Integer ? (Integer) ratingObj : ((Number) ratingObj).intValue();
            
            if (rating < 0 || rating > 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5");
            }
            movie.setRatingCollins(rating);
        }

        Movie updatedMovie = movieRepository.save(movie);
        return ResponseEntity.ok(updatedMovie);
    }

    // ─── DELETE MOVIE ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found with id: " + id));

        movieRepository.delete(movie);
        return ResponseEntity.noContent().build();
    }

    // ─── GET MOVIE STATS ────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getMovieStats() {
        long watchlist = movieRepository.countByStatus("WATCHLIST");
        long watching = movieRepository.countByStatus("WATCHING");
        long watched = movieRepository.countByStatus("WATCHED");
        long total = movieRepository.count();

        Map<String, Long> stats = Map.of(
                "total", total,
                "watchlist", watchlist,
                "watching", watching,
                "watched", watched
        );

        return ResponseEntity.ok(stats);
    }
}
