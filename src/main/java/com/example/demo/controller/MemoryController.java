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
import java.time.format.DateTimeFormatter;
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

    @PostMapping
    public Memory saveMemory(@RequestBody Memory memory) {
        if (memory.getDateCreated() == null) {
            memory.setDateCreated(LocalDate.now());
        }
        Memory saved = repository.save(memory);
        
        // Send notification to partner
        notificationService.notifyPartner(
            memory.getUploadedBy(),
            "New memory added 💛",
            memory.getTitle() != null ? memory.getTitle() : "Check it out in Our Space"
        );
        
        return saved;
    }

    // ✅ SUPPORT MULTIPLE FILE UPLOADS TO CLOUDINARY
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        
        try {
            for (MultipartFile file : files) {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                            "folder", "our-space-memories", // Optional: organize in folder
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            // Optional: Delete from Cloudinary as well
            Memory memory = repository.findById(id).orElse(null);
            if (memory != null && memory.getImageUrls() != null) {
                for (String url : memory.getImageUrls()) {
                    // Extract public ID from URL and delete from Cloudinary
                    try {
                        String publicId = extractPublicIdFromUrl(url);
                        if (publicId != null) {
                            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        }
                    } catch (Exception e) {
                        // Log error but continue
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

    private String extractPublicIdFromUrl(String url) {
        try {
            // Extract public ID from Cloudinary URL
            // Example: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/public_id.jpg
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            String publicId = lastPart.substring(0, lastPart.lastIndexOf("."));
            
            // Check if there's a folder structure
            for (int i = parts.length - 2; i >= 0; i--) {
                if (parts[i].equals("upload")) {
                    StringBuilder fullId = new StringBuilder();
                    for (int j = i + 1; j < parts.length - 1; j++) {
                        // Skip version part (v1234567890)
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
