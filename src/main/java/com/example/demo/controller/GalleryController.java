package com.example.demo.controller;

import com.example.demo.entity.GalleryItem;
import com.example.demo.repository.GalleryRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/gallery")
@CrossOrigin(origins = "*")
public class GalleryController {

    private final GalleryRepository repo;

    public GalleryController(GalleryRepository repo) { this.repo = repo; }

    @GetMapping
    public List<GalleryItem> getAll() { return repo.findAll(); }

    // Filter by type (IMAGE/VIDEO)
    @GetMapping("/type/{type}")
    public List<GalleryItem> getByType(@PathVariable String type) {
        return repo.findByType(type.toUpperCase());
    }

    // Favorites
    @GetMapping("/favorites")
    public List<GalleryItem> getFavorites() {
        return repo.findByFavoriteTrue();
    }

    // Toggle favorite
    @PutMapping("/{id}/favorite")
    public GalleryItem toggleFavorite(@PathVariable Long id) {
        GalleryItem item = repo.findById(id).orElseThrow();
        item.setFavorite(!item.isFavorite());
        return repo.save(item);
    }

    // Multi‑file upload
    @PostMapping(consumes = "multipart/form-data")
    public List<GalleryItem> uploadMultiple(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("files") MultipartFile[] files) throws Exception {

        List<GalleryItem> saved = new ArrayList<>();

        String folder = System.getProperty("user.dir") + "/uploads/";
        new File(folder).mkdirs();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(folder, filename);
            file.transferTo(dest);

            GalleryItem item = new GalleryItem();
            item.setTitle(title);
            item.setType(type);
            item.setFileUrl("/uploads/" + filename);
            saved.add(repo.save(item));
        }

        return saved;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}