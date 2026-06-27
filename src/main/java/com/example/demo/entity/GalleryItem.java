package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gallery_items")
public class GalleryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String type; // IMAGE or VIDEO
    private String fileUrl;
    
    @Column(columnDefinition = "TEXT")
    private String fileHash; // For duplicate detection
    
    private boolean favorite = false;
    
    @Column(name = "date_created", updatable = false)
    private java.time.LocalDateTime dateCreated = java.time.LocalDateTime.now();

    // Constructors
    public GalleryItem() {}

    public GalleryItem(String title, String type, String fileUrl) {
        this.title = title;
        this.type = type;
        this.fileUrl = fileUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public java.time.LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(java.time.LocalDateTime dateCreated) { this.dateCreated = dateCreated; }
}