package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.entity.Memory;
import com.example.demo.repository.MemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memories")
@CrossOrigin("*")
public class MemoryController {

    @Autowired
    private MemoryRepository repository;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    public List<Memory> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Memory saveMemory(@RequestBody Memory memory) {
        if (memory.getDateCreated() == null) {
            memory.setDateCreated(LocalDate.now());
        }
        return repository.save(memory);
    }

    // ✅ CLOUDINARY UPLOAD (REPLACES YOUR OLD FILE SYSTEM CODE)
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.emptyMap()
        );

        String imageUrl = uploadResult.get("secure_url").toString();

        return imageUrl;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}