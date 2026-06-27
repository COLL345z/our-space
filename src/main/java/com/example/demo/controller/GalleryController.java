package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.entity.GalleryItem;
import com.example.demo.repository.GalleryRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final GalleryRepository repo;
    private final Cloudinary cloudinary;

    public GalleryController(GalleryController repo, Cloudinary cloudinary) {
        this.repo = repo;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    public List<GalleryItem> getAll() {
        return repo.findAll();
    }

    @GetMapping("/type/{type}")
    public List<GalleryItem> getByType(@PathVariable String type) {
        return repo.findByType(type.toUpperCase());
    }

    @GetMapping("/favorites")
    public List<GalleryItem> getFavorites() {
        return repo.findByFavoriteTrue();
    }

    @PutMapping("/{id}/favorite")
    public GalleryItem toggleFavorite(@PathVariable Long id) {
        GalleryItem item = repo.findById(id).orElseThrow();
        item.setFavorite(!item.isFavorite());
        return repo.save(item);
    }

    @PostMapping(consumes = "multipart/form-data")
    public List<GalleryItem> uploadMultiple(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("files") MultipartFile[] files) throws Exception {

        List<GalleryItem> saved = new ArrayList<>();
        Set<String> existingHashes = new HashSet<>();
        
        // Get existing file hashes to detect duplicates
        for (GalleryItem existing : repo.findAll()) {
            if (existing.getFileHash() != null) {
                existingHashes.add(existing.getFileHash());
            }
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // Generate hash for duplicate detection
            byte[] bytes = file.getBytes();
            String hash = Base64.getEncoder().encodeToString(
                Arrays.copyOfRange(bytes, 0, Math.min(bytes.length, 1024))
            );

            // Skip duplicate
            if (existingHashes.contains(hash)) {
                continue;
            }

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

            saved.add(repo.save(item));
        }

        return saved;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}