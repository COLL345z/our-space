package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Round {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questionText;
    private String mode; // "SHARE" or "GUESS"
    private String targetUsername; // GUESS mode only — whose "truth" is being guessed
    private LocalDate createdAt;
    private Boolean guessCorrect; // self-scored after reveal, GUESS mode only

    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getTargetUsername() { return targetUsername; }
    public void setTargetUsername(String targetUsername) { this.targetUsername = targetUsername; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public Boolean getGuessCorrect() { return guessCorrect; }
    public void setGuessCorrect(Boolean guessCorrect) { this.guessCorrect = guessCorrect; }
}
