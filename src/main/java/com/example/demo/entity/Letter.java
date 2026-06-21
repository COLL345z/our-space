package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String sender;
    private String receiver;
    private String date;
    private String openDate;
// in Letter entity, after other fields:
private Long parentId;   // null if this is a top‑level letter, otherwise the parent letter's id

// getters & setters
public Long getParentId() { return parentId; }
public void setParentId(Long parentId) { this.parentId = parentId; }

    @Column(name = "is_read")      // renamed to avoid MySQL reserved word
    private boolean isRead = false;

    @ElementCollection
    private List<String> imagePaths = new ArrayList<>();

    private String status = "SENT";

    // Getters and setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getOpenDate() { return openDate; }
    public void setOpenDate(String openDate) { this.openDate = openDate; }

    public boolean isRead() { return isRead; }          // note: getter name "isRead"
    public void setRead(boolean read) { this.isRead = read; }   // setter still works for JPA

    public List<String> getImagePaths() { return imagePaths; }
    public void setImagePaths(List<String> imagePaths) { this.imagePaths = imagePaths; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}