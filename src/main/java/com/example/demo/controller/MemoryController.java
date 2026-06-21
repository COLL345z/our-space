package com.example.demo.controller;

import com.example.demo.entity.Memory;
import com.example.demo.repository.MemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/memories")
@CrossOrigin("*")
public class MemoryController {

    @Autowired
    private MemoryRepository repository;

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

@PostMapping("/upload")
public List<String> uploadImages(
        @RequestParam("files") MultipartFile[] files
) throws Exception {

    List<String> urls = new ArrayList<>();

    for(MultipartFile file : files){

        String filename =
                UUID.randomUUID()
                + "_"
                + file.getOriginalFilename();

        Path path = Paths.get(
                "uploads/" + filename
        );

        Files.copy(
                file.getInputStream(),
                path,
                StandardCopyOption.REPLACE_EXISTING
        );

        urls.add(
                "/uploads/" + filename
        );
    }

    return urls;
}

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
    
}