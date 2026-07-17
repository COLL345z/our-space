package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.repository.MessageRepository;
import com.example.demo.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final CurrentUserResolver currentUserResolver;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageRepository messageRepository, 
                             CurrentUserResolver currentUserResolver,
                             SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.currentUserResolver = currentUserResolver;
        this.messagingTemplate = messagingTemplate;
    }

    // ─── REST Endpoint ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<?> getHistory(HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        List<Message> history = messageRepository.findAllByOrderByIdAsc();
        return ResponseEntity.ok(history);
    }

    // ─── 🔥 WebSocket Endpoint ──────────────────────────────────────
    // Handles incoming messages from the client
    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public Message sendMessage(@Payload Message message) {
        // Set timestamp if not set
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now().toString());
        }
        
        // 🔥 Save to database
        Message saved = messageRepository.save(message);
        
        return saved;
    }
}
