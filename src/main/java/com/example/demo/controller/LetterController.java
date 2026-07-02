package com.example.demo.controller;

import com.example.demo.entity.Letter;
import com.example.demo.repository.LetterRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    // ── INBOX ──
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

    // ⭐ JSON endpoint (text-only letters)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createLetterJson(
            @RequestBody Map<String, Object> body,
            HttpSession session) {

        String sender = (String) session.getAttribute("user");
        if (sender == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Letter letter = new Letter();
        letter.setTitle((String) body.get("title"));
        letter.setContent((String) body.get("content"));
        letter.setSender(sender);
        letter.setReceiver((String) body.get("receiver"));
        letter.setDate((String) body.get("date"));

        if (body.get("openDate") != null && !body.get("openDate").toString().isEmpty()) {
            letter.setOpenDate(body.get("openDate").toString());
        }

        String status = body.get("status") != null ? body.get("status").toString().toUpperCase() : "SENT";
        letter.setStatus(status);

        if (body.get("parentId") != null && !body.get("parentId").toString().isEmpty()
                && !body.get("parentId").toString().equals("null")) {
            letter.setParentId(Long.parseLong(body.get("parentId").toString()));
        }

        Letter saved = letterRepository.save(letter);
        return ResponseEntity.ok(saved);
    }

    // ⭐ Multipart endpoint (letters with images) — NO defaultValue
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createLetterMultipart(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("receiver") String receiver,
            @RequestPart("date") String date,
            @RequestPart(value = "openDate", required = false) String openDate,
            @RequestPart(value = "status", required = false) String status,
            @RequestPart(value = "parentId", required = false) String parentId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpSession session) throws Exception {

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

    // ── DELETE ──
    @DeleteMapping("/{id}")
    public void deleteLetter(@PathVariable Long id) {
        letterRepository.deleteById(id);
    }

    // ── MARK READ ──
    @PutMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        letterRepository.findById(id).ifPresent(letter -> {
            letter.setRead(true);
            letterRepository.save(letter);
        });
    }
}
