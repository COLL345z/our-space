package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.entity.Memory;
import com.example.demo.repository.MemoryRepository;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memories")
public class MemoryController {

    @Autowired
    private MemoryRepository repository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<Memory> getAll() {
        return repository.findAll();
    }

    // ─── Save Memory (with support for all types) ──────────────────
    @PostMapping
    public Memory saveMemory(@RequestBody Memory memory) {
        if (memory.getDateCreated() == null) {
            memory.setDateCreated(LocalDate.now());
        }
        
        // Set memory type based on category if not set
        if (memory.getMemoryType() == null || memory.getMemoryType().isEmpty()) {
            memory.setMemoryType(memory.getCategory());
        }
        
        // For TEXT type, store content in textContent
        if ("TEXT".equals(memory.getCategory()) && memory.getTextContent() != null) {
            memory.setContent(memory.getTextContent());
        }
        
        // For LINK type, store URL in linkUrl and content
        if ("LINK".equals(memory.getCategory()) && memory.getLinkUrl() != null) {
            memory.setContent(memory.getLinkUrl());
        }

        Memory saved = repository.save(memory);
        
        // Send notification to partner
        String title = memory.getTitle() != null ? memory.getTitle() : "New memory added 💛";
        String message = switch (memory.getCategory()) {
            case "LINK" -> "Shared a link with you 🔗";
            case "SCREENSHOT" -> "Shared a screenshot 📱";
            case "TEXT" -> "Shared a text note 📝";
            default -> "Check it out in Our Space";
        };
        
        notificationService.notifyPartner(memory.getUploadedBy(), title, message);
        
        return saved;
    }

    // ─── Upload Multiple Images ─────────────────────────────────────
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        
        try {
            for (MultipartFile file : files) {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                            "folder", "our-space-memories",
                            "resource_type", "auto"
                        )
                );
                String imageUrl = uploadResult.get("secure_url").toString();
                urls.add(imageUrl);
            }
            return ResponseEntity.ok(urls);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // ─── Delete Memory ──────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            Memory memory = repository.findById(id).orElse(null);
            if (memory != null && memory.getImageUrls() != null) {
                for (String url : memory.getImageUrls()) {
                    try {
                        String publicId = extractPublicIdFromUrl(url);
                        if (publicId != null) {
                            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
                    }
                }
            }
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ─── Get by Category ────────────────────────────────────────────
    @GetMapping("/category/{category}")
    public List<Memory> getByCategory(@PathVariable String category) {
        return repository.findByCategory(category);
    }

    // ─── Helper: Extract Public ID from Cloudinary URL ─────────────
    private String extractPublicIdFromUrl(String url) {
        try {
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            String publicId = lastPart.substring(0, lastPart.lastIndexOf("."));
            
            for (int i = parts.length - 2; i >= 0; i--) {
                if (parts[i].equals("upload")) {
                    StringBuilder fullId = new StringBuilder();
                    for (int j = i + 1; j < parts.length - 1; j++) {
                        if (parts[j].matches("v\\d+")) continue;
                        if (fullId.length() > 0) fullId.append("/");
                        fullId.append(parts[j]);
                    }
                    if (fullId.length() > 0) {
                        fullId.append("/");
                    }
                    fullId.append(publicId);
                    return fullId.toString();
                }
            }
            return publicId;
        } catch (Exception e) {
            return null;
        }
    }
}
