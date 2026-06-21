package com.example.demo.controller;

import com.example.demo.entity.Letter;
import com.example.demo.repository.LetterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/letters")
@CrossOrigin("*")
public class LetterController {

    @Autowired
    private LetterRepository letterRepository;

    // ── existing endpoints (GET inbox/sent, POST, DELETE, markRead) remain unchanged ──

    // GET inbox for a user (only sent letters)
    @GetMapping("/inbox/{user}")
    public List<Letter> getInbox(@PathVariable String user) {
        return letterRepository.findByReceiverAndStatus(user, "SENT");
    }
    @GetMapping("/{id}/replies")
public List<Letter> getReplies(@PathVariable Long id) {
    return letterRepository.findByParentId(id);
}

    // GET sent by user (only sent)
    @GetMapping("/sent/{user}")
    public List<Letter> getSent(@PathVariable String user) {
        return letterRepository.findBySenderAndStatus(user, "SENT");
    }

    // ── NEW: get drafts for a user ──
    @GetMapping("/drafts/{user}")
    public List<Letter> getDrafts(@PathVariable String user) {
        return letterRepository.findBySenderAndStatus(user, "DRAFT");
    }

    // ── CREATE letter (handles both SENT and DRAFT) ──
    @PostMapping
    public Letter createLetter(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("sender") String sender,
            @RequestParam("receiver") String receiver,
            @RequestParam("date") String date,
            @RequestParam(value = "openDate", required = false) String openDate,
            @RequestParam(value = "status", defaultValue = "SENT") String status,
            @RequestParam(value = "images", required = false) MultipartFile[] images
    ) throws Exception {

        Letter letter = new Letter();
        letter.setTitle(title);
        letter.setContent(content);
        letter.setSender(sender);
        letter.setReceiver(receiver);
        letter.setDate(date);   // works because setter is still setRead()
        letter.setOpenDate(openDate);
        letter.setStatus(status.toUpperCase());   // "SENT" or "DRAFT"

        // handle images (same as before)
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

    // ── UPDATE an existing letter (e.g., draft → sent) ──
    @PutMapping("/{id}")
    public Letter updateLetter(@PathVariable Long id, @RequestBody Letter updated) {
        return letterRepository.findById(id).map(letter -> {
            letter.setTitle(updated.getTitle());
            letter.setContent(updated.getContent());
            letter.setDate(updated.getDate());
            letter.setOpenDate(updated.getOpenDate());
            letter.setRead(true);   // works because setter is still setRead()
            letter.setStatus(updated.getStatus());      // important: change DRAFT → SENT
            letter.setReceiver(updated.getReceiver());  // maybe changed?
            // images are not updated here; you could add a separate upload endpoint if needed
            return letterRepository.save(letter);
        }).orElse(null);
    }

    // ── DELETE, markRead unchanged ──
    @DeleteMapping("/{id}")
    public void deleteLetter(@PathVariable Long id) {
        letterRepository.deleteById(id);
    }

    @PutMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        letterRepository.findById(id).ifPresent(letter -> {
            letter.setRead(false);
            letterRepository.save(letter);
        });
    }
}