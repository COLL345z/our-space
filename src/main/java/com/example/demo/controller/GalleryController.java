package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.entity.GalleryItem;
import com.example.demo.repository.GalleryRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.*;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final GalleryRepository repository;
    private final Cloudinary cloudinary;

    // ✅ FIXED: Correct constructor parameter name
    public GalleryController(GalleryRepository repository, Cloudinary cloudinary) {
        this.repository = repository;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    public List<GalleryItem> getAll() {
        return repository.findAll();
    }

    @GetMapping("/type/{type}")
    public List<GalleryItem> getByType(@PathVariable String type) {
        return repository.findByType(type.toUpperCase());
    }

    @GetMapping("/favorites")
    public List<GalleryItem> getFavorites() {
        return repository.findByFavoriteTrue();
    }

    @PutMapping("/{id}/favorite")
    public GalleryItem toggleFavorite(@PathVariable Long id) {
        GalleryItem item = repository.findById(id).orElseThrow();
        item.setFavorite(!item.isFavorite());
        return repository.save(item);
    }

    @PostMapping(consumes = "multipart/form-data")
    public List<GalleryItem> uploadMultiple(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("files") MultipartFile[] files) throws Exception {

        List<GalleryItem> saved = new ArrayList<>();
        Set<String> existingHashes = new HashSet<>();
        
        // Get existing file hashes to detect duplicates
        for (GalleryItem existing : repository.findAll()) {
            if (existing.getFileHash() != null) {
                existingHashes.add(existing.getFileHash());
            }
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // ✅ Generate proper SHA-256 hash for duplicate detection
            byte[] bytes = file.getBytes();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(bytes);
            String hash = Base64.getEncoder().encodeToString(hashBytes);

            // Skip duplicate
            if (existingHashes.contains(hash)) {
                continue;
            }

            // Upload to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                    bytes,
                    ObjectUtils.asMap(
                        "folder", "our-space-gallery",
                        "resource_type", "auto"
                    )
            );

            String url = uploadResult.get("secure_url").toString();

            GalleryItem item = new GalleryItem();
            item.setTitle(title);
            item.setType(type.toUpperCase());
            item.setFileUrl(url);
            item.setFileHash(hash);

            saved.add(repository.save(item));
        }

        return saved;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}