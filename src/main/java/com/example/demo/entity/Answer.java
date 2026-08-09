package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Answer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roundId;
    private String username;
    @Column(columnDefinition = "TEXT")
    private String text;

    public Long getId() { return id; }
    public Long getRoundId() { return roundId; }
    public void setRoundId(Long roundId) { this.roundId = roundId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
