package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "memories")
public class Memory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;  // "LINK", "SCREENSHOT", "TEXT"
    private String description;
    private String uploadedBy;

    @ElementCollection
    @CollectionTable(name = "memory_images", joinColumns = @JoinColumn(name = "memory_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> imageUrls = new ArrayList<>();

    private LocalDate dateCreated;

    // ✅ Fields for different memory types
    @Column(length = 2048)
    private String linkUrl;      // For LINKS tab

    @Column(columnDefinition = "TEXT")
    private String textContent;  // For TEXT tab

    @Column(columnDefinition = "TEXT")
    private String content;      // General content field

    private String source;       // Where it came from (WhatsApp, TikTok, etc.)
    private String memoryType;   // "TEXT", "LINK", "SCREENSHOT"
    private boolean isFavorite = false;

    // Constructors
    public Memory() {}

    public Memory(String title, String category, String description, String uploadedBy, 
                  List<String> imageUrls, String linkUrl, String textContent) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.uploadedBy = uploadedBy;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.linkUrl = linkUrl;
        this.textContent = textContent;
        this.dateCreated = LocalDate.now();
        this.memoryType = category;
    }

    // ─── Getters and Setters ──────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public LocalDate getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDate dateCreated) { this.dateCreated = dateCreated; }

    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getMemoryType() { return memoryType; }
    public void setMemoryType(String memoryType) { this.memoryType = memoryType; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
