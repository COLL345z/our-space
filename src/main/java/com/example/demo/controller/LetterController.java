package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.entity.Letter;
import com.example.demo.repository.LetterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/letters")
@CrossOrigin("*")
public class LetterController {

    @Autowired
    private LetterRepository letterRepository;

    @Autowired
    private Cloudinary cloudinary;

    // GET inbox (only SENT)
    @GetMapping("/inbox/{user}")
    public List<Letter> getInbox(@PathVariable String user) {
        return letterRepository.findByReceiverAndStatus(user, "SENT");
    }

    // GET sent letters
    @GetMapping("/sent/{user}")
    public List<Letter> getSent(@PathVariable String user) {
        return letterRepository.findBySenderAndStatus(user, "SENT");
    }

    // GET drafts
    @GetMapping("/drafts/{user}")
    public List<Letter> getDrafts(@PathVariable String user) {
        return letterRepository.findBySenderAndStatus(user, "DRAFT");
    }

    // GET replies
    @GetMapping("/{id}/replies")
    public List<Letter> getReplies(@PathVariable Long id) {
        return letterRepository.findByParentId(id);
    }

    // CREATE LETTER (FIXED CLOUDINARY UPLOAD)
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
        letter.setDate(date);
        letter.setOpenDate(openDate);
        letter.setStatus(status.toUpperCase());

        // ✅ CLOUDINARY FIX
        if (images != null && images.length > 0) {
            List<String> urls = new ArrayList<>();

            for (MultipartFile file : images) {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.emptyMap()
                );

                String url = uploadResult.get("secure_url").toString();
                urls.add(url);
            }

            letter.setImagePaths(urls);
        }

        return letterRepository.save(letter);
    }

    // UPDATE LETTER
    @PutMapping("/{id}")
    public Letter updateLetter(@PathVariable Long id, @RequestBody Letter updated) {
        return letterRepository.findById(id).map(letter -> {
            letter.setTitle(updated.getTitle());
            letter.setContent(updated.getContent());
            letter.setDate(updated.getDate());
            letter.setOpenDate(updated.getOpenDate());
            letter.setRead(true);
            letter.setStatus(updated.getStatus());
            letter.setReceiver(updated.getReceiver());
            return letterRepository.save(letter);
        }).orElse(null);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteLetter(@PathVariable Long id) {
        letterRepository.deleteById(id);
    }

    // MARK AS READ
    @PutMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        letterRepository.findById(id).ifPresent(letter -> {
            letter.setRead(true);
            letterRepository.save(letter);
        });
    }
}