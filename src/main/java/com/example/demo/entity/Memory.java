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
    private String category;
    private String description;
    private String uploadedBy;

    @ElementCollection
    @CollectionTable(name = "memory_images", joinColumns = @JoinColumn(name = "memory_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    private List<String> imageUrls = new ArrayList<>();

    private LocalDate dateCreated;

    // ✅ New fields
    @Column(length = 2048)  // longer URL possible
    private String linkUrl;

    @Column(columnDefinition = "TEXT")
    private String textContent;

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
    }

    // ... keep existing getters/setters ...
      // Getters and Setters
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


    // ✅ Add getters & setters for new fields
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
}
