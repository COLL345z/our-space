package com.example.demo.controller;

import com.example.demo.entity.Letter;
import com.example.demo.repository.LetterRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.MediaType;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/letters")

public class LetterController {

    @Autowired
    private LetterRepository letterRepository;

    // ── GET CURRENT USER ──
    @GetMapping("/api/current-user")
    public ResponseEntity<String> getCurrentUser(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }
        return ResponseEntity.ok(user);
    }

    // ── INBOX (SESSION BASED) ──
    @GetMapping("/inbox")
    public List<Letter> getInbox(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) return Collections.emptyList();
        return letterRepository.findByReceiverAndStatus(user, "SENT");
    }

    // ── SENT ──
    @GetMapping("/sent")
    public List<Letter> getSent(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) return Collections.emptyList();
        return letterRepository.findBySenderAndStatus(user, "SENT");
    }

    // ── DRAFTS ──
    @GetMapping("/drafts")
    public List<Letter> getDrafts(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) return Collections.emptyList();
        return letterRepository.findBySenderAndStatus(user, "DRAFT");
    }

    // ── REPLIES ──
    @GetMapping("/{id}/replies")
    public List<Letter> getReplies(@PathVariable Long id) {
        return letterRepository.findByParentId(id);
    }

   // ── CREATE LETTER (FIXED) ──
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createLetter(
        @RequestPart("title") String title,
        @RequestPart("content") String content,
        @RequestPart("receiver") String receiver,
        @RequestPart("date") String date,
        @RequestPart(value = "openDate", required = false) String openDate,
        @RequestPart(value = "status", defaultValue = "SENT") String status,
        @RequestPart(value = "parentId", required = false) String parentId,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        HttpSession session
) throws Exception {

    String sender = (String) session.getAttribute("user");
    if (sender == null) {
        return ResponseEntity.status(401).body("Not logged in");
    }

    Letter letter = new Letter();
    letter.setTitle(title);
    letter.setContent(content);
    letter.setSender(sender);
    letter.setReceiver(receiver);
    letter.setDate(date);
    letter.setOpenDate(openDate != null ? openDate.trim() : null);
    letter.setStatus(status != null ? status.toUpperCase() : "SENT");

    if (parentId != null && !parentId.trim().isEmpty() && !parentId.equals("null")) {
        letter.setParentId(Long.parseLong(parentId.trim()));
    }

    // Handle images
    if (images != null && !images.isEmpty()) {
        List<String> urls = new ArrayList<>();
        String uploadDir = System.getProperty("user.dir") + "/uploads/letters/";
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        for (MultipartFile file : images) {
            if (file.isEmpty()) continue;
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/letters/")
                    .path(filename)
                    .toUriString();
            urls.add(fileUrl);
        }
        letter.setImagePaths(urls);
    }

    Letter saved = letterRepository.save(letter);
    return ResponseEntity.ok(saved);
}
    // ── UPDATE ──
    @PutMapping("/{id}")
    public Letter updateLetter(@PathVariable Long id, @RequestBody Letter updated) {
        return letterRepository.findById(id).map(letter -> {
            letter.setTitle(updated.getTitle());
            letter.setContent(updated.getContent());
            letter.setDate(updated.getDate());
            letter.setOpenDate(updated.getOpenDate());
            letter.setStatus(updated.getStatus());
            letter.setReceiver(updated.getReceiver());
            return letterRepository.save(letter);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteLetter(@PathVariable Long id) {
        letterRepository.deleteById(id);
    }

    @PutMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        letterRepository.findById(id).ifPresent(letter -> {
            letter.setRead(true);
            letterRepository.save(letter);
        });
    }
}
