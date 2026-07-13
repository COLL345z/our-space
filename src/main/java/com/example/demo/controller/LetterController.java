package com.example.demo.controller;

import com.example.demo.entity.Letter;
import com.example.demo.repository.LetterRepository;
import com.example.demo.security.CurrentUserResolver;
import com.example.demo.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CurrentUserResolver currentUserResolver;

    @Autowired
    private NotificationService notificationService;

    private String getCurrentUser(HttpServletRequest request, HttpSession session) {
        return currentUserResolver.resolve(request, session);
    }

    @GetMapping("/api/current-user")
    public ResponseEntity<String> getCurrentUserEndpoint(HttpServletRequest request,
                                                         HttpSession session) {
        String user = getCurrentUser(request, session);
        if (user == null) return ResponseEntity.status(401).body("Not logged in");
        return ResponseEntity.ok(user);
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<Letter>> getInbox(HttpServletRequest request,
                                                  HttpSession session) {
        String user = getCurrentUser(request, session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(letterRepository.findByReceiverAndStatus(user, "SENT"));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<Letter>> getSent(HttpServletRequest request,
                                                 HttpSession session) {
        String user = getCurrentUser(request, session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(letterRepository.findBySenderAndStatus(user, "SENT"));
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<Letter>> getDrafts(HttpServletRequest request,
                                                   HttpSession session) {
        String user = getCurrentUser(request, session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(letterRepository.findBySenderAndStatus(user, "DRAFT"));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<Letter>> getReplies(@PathVariable Long id) {
        return ResponseEntity.ok(letterRepository.findByParentId(id));
    }

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
            HttpServletRequest request,
            HttpSession session) throws Exception {

        String sender = getCurrentUser(request, session);
        if (sender == null) return ResponseEntity.status(401).body("Not logged in");

        Letter letter = new Letter();

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
            letter.setTitle(title);
            letter.setContent(content);
            letter.setReceiver(receiver);
            letter.setDate(date);
            letter.setOpenDate(openDate);
            letter.setStatus(status.toUpperCase());
            letter.setParentId(parentId);
        }

        letter.setSender(sender);

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

        Letter saved = letterRepository.save(letter);

        // ✅ Send notification for new letters (only if status is SENT, not DRAFT)
        if (saved.getStatus().equals("SENT") && saved.getReceiver() != null) {
            String notificationTitle = "💌 New letter from " + sender;
            String notificationBody = saved.getTitle() != null ? 
                saved.getTitle() : "You received a new love letter";
            
            notificationService.notifyPartner(
                saved.getReceiver(),  // Send notification to the receiver
                notificationTitle,
                notificationBody
            );
        }

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Letter> updateLetter(@PathVariable Long id,
                                                @RequestBody Letter updated) {
        return letterRepository.findById(id).map(letter -> {
            letter.setTitle(updated.getTitle());
            letter.setContent(updated.getContent());
            letter.setDate(updated.getDate());
            letter.setOpenDate(updated.getOpenDate());
            letter.setStatus(updated.getStatus());
            letter.setReceiver(updated.getReceiver());
            return ResponseEntity.ok(letterRepository.save(letter));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLetter(@PathVariable Long id) {
        if (letterRepository.existsById(id)) {
            letterRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id) {
        Optional<Letter> opt = letterRepository.findById(id);
        if (opt.isPresent()) {
            Letter letter = opt.get();
            letter.setRead(true);
            letterRepository.save(letter);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
