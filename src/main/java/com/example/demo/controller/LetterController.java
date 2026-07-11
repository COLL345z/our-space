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

    @GetMapping("/api/current-user")
    public ResponseEntity<String> getCurrentUser(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not logged in");
        return ResponseEntity.ok(user);
    }

    @GetMapping("/inbox")
    public List<Letter> getInbox(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) return Collections.emptyList();
        return letterRepository.findByReceiverAndStatus(user, "SENT");
    }

    @GetMapping("/sent")
    public List<Letter> getSent(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) return Collections.emptyList();
        return letterRepository.findBySenderAndStatus(user, "SENT");
    }

    @GetMapping("/drafts")
    public List<Letter> getDrafts(HttpSession session) {
        String user = (String) session.getAttribute("user");
        if (user == null) return Collections.emptyList();
        return letterRepository.findBySenderAndStatus(user, "DRAFT");
    }

    @GetMapping("/{id}/replies")
    public List<Letter> getReplies(@PathVariable Long id) {
        return letterRepository.findByParentId(id);
    }

    // ⭐ SINGLE endpoint — handles BOTH JSON (text-only) and multipart (with images)
    @PostMapping
    public ResponseEntity<?> createLetter(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "receiver", required = false) String receiver,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "openDate", required = false) String openDate,
            @RequestParam(value = "status", defaultValue = "SENT") String status,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestBody(required = false) Map<String, Object> jsonBody,
            HttpSession session) throws Exception {

        String sender = (String) session.getAttribute("user");
        if (sender == null) return ResponseEntity.status(401).body("Not logged in");

        Letter letter = new Letter();

        // Check if this is a JSON request
        if (jsonBody != null && !jsonBody.isEmpty()) {
            letter.setTitle((String) jsonBody.get("title"));
            letter.setContent((String) jsonBody.get("content"));
            letter.setReceiver((String) jsonBody.get("receiver"));
            letter.setDate((String) jsonBody.get("date"));
            if (jsonBody.get("openDate") != null) {
                letter.setOpenDate(jsonBody.get("openDate").toString());
            }
            letter.setStatus(jsonBody.get("status") != null ? 
                jsonBody.get("status").toString().toUpperCase() : "SENT");
            if (jsonBody.get("parentId") != null) {
                Object pid = jsonBody.get("parentId");
                if (pid instanceof Integer) letter.setParentId(((Integer) pid).longValue());
                else if (pid instanceof Long) letter.setParentId((Long) pid);
                else if (pid instanceof String && !pid.toString().isEmpty()) {
                    letter.setParentId(Long.parseLong(pid.toString()));
                }
            }
        } else {
            // Multipart request
            letter.setTitle(title);
            letter.setContent(content);
            letter.setReceiver(receiver);
            letter.setDate(date);
            letter.setOpenDate(openDate);
            letter.setStatus(status.toUpperCase());
            letter.setParentId(parentId);
        }

        letter.setSender(sender);

        // Handle images (multipart only)
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
                        .path("/uploads/letters/").path(filename).toUriString();
                urls.add(fileUrl);
            }
            letter.setImagePaths(urls);
        }

        return ResponseEntity.ok(letterRepository.save(letter));
    }

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
