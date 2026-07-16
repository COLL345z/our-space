package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.repository.MessageRepository;
import com.example.demo.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final CurrentUserResolver currentUserResolver;

    public MessageController(MessageRepository messageRepository, CurrentUserResolver currentUserResolver) {
        this.messageRepository = messageRepository;
        this.currentUserResolver = currentUserResolver;
    }

    // History only — new messages arrive live over the WebSocket at
    // /topic/messages. This endpoint is for the initial load when opening
    // the chat screen, and as a fallback if the socket ever drops.
    @GetMapping
    public ResponseEntity<?> getHistory(HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        List<Message> history = messageRepository.findAllByOrderByIdAsc();
        return ResponseEntity.ok(history);
    }
}
