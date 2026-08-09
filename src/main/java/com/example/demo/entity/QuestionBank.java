package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class QuestionBank {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String mode; // "SHARE" or "GUESS"

    public Long getId() { return id; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
}
