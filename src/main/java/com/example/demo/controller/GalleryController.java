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
@CrossOrigin(origins = "*")
public class GalleryController {

    private final GalleryRepository repo;
    private final Cloudinary cloudinary;

    public GalleryController(GalleryRepository repo, Cloudinary cloudinary) {
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

    // ✅ CLOUDINARY UPLOAD
    @PostMapping(consumes = "multipart/form-data")
    public List<GalleryItem> uploadMultiple(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("files") MultipartFile[] files) throws Exception {

        List<GalleryItem> saved = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            String url = uploadResult.get("secure_url").toString();

            GalleryItem item = new GalleryItem();
            item.setTitle(title);
            item.setType(type);
            item.setFileUrl(url);

            saved.add(repo.save(item));
        }

        return saved;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}