package com.example.demo.controller;

import com.example.demo.entity.Letter;
import com.example.demo.repository.LetterRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/letters")
// @CrossOrigin(origins = "*", allowCredentials = "true")
public class LetterController {

    @Autowired
    private LetterRepository letterRepository;

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

    // ── CREATE LETTER ──
    @PostMapping
    public Letter createLetter(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String receiver,
            @RequestParam String date,
            @RequestParam(required = false) String openDate,
            @RequestParam(defaultValue = "SENT") String status,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) MultipartFile[] images,
            HttpSession session
    ) throws Exception {

        String sender = (String) session.getAttribute("user");
        if (sender == null) throw new RuntimeException("Not logged in");

        Letter letter = new Letter();
        letter.setTitle(title);
        letter.setContent(content);
        letter.setSender(sender);
        letter.setReceiver(receiver);
        letter.setDate(date);
        letter.setOpenDate(openDate);
        letter.setStatus(status.toUpperCase());

        if (parentId != null) {
            letter.setParentId(parentId);
        }

        // images (unchanged)
        if (images != null && images.length > 0) {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : images) {
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path path = Paths.get("uploads/letters/" + filename);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                urls.add("/uploads/letters/" + filename);
            }
            letter.setImagePaths(urls);
        }

        return letterRepository.save(letter);
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