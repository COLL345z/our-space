package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String date;
    private String status;          // PLANNED or COMPLETED
    private int ratingRehema;       // 0–5, Rehema's rating
    private int ratingCollins;      // 0–5, Collins' rating

    public Activity() {}

    public Activity(String title, String description, String date, 
                    String status, int ratingRehema, int ratingCollins) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.status = status;
        this.ratingRehema = ratingRehema;
        this.ratingCollins = ratingCollins;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRatingRehema() { return ratingRehema; }
    public void setRatingRehema(int ratingRehema) { this.ratingRehema = ratingRehema; }

    public int getRatingCollins() { return ratingCollins; }
    public void setRatingCollins(int ratingCollins) { this.ratingCollins = ratingCollins; }
}