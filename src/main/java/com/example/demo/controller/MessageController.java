package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.entity.MessageReaction;
import com.example.demo.repository.MessageReactionRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final MessageReactionRepository reactionRepository;
    private final CurrentUserResolver currentUserResolver;

    public MessageController(MessageRepository messageRepository, MessageReactionRepository reactionRepository,
                              CurrentUserResolver currentUserResolver) {
        this.messageRepository = messageRepository;
        this.reactionRepository = reactionRepository;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<?> getHistory(HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(messageRepository.findAllByOrderByIdAsc());
    }

    // Client polls, so no WebSocket broadcast needed here — just persist and
    // return the saved message; the sender's own next poll (and the
    // partner's) will pick it up.
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Message incoming, HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();
        if (incoming.getContent() == null || incoming.getContent().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Sender identity ALWAYS comes from the authenticated session, never
        // from the request body — a client can't send a message "as" the
        // other person even if it puts a different senderUsername in the JSON.
        Message message = new Message(username, incoming.getContent(), Instant.now().toString());

        if (incoming.getReplyToId() != null) {
            messageRepository.findById(incoming.getReplyToId()).ifPresent(original -> {
                message.setReplyToId(original.getId());
                message.setReplyToSenderUsername(original.getSenderUsername());
                String snippet = original.getContent();
                message.setReplyToContent(snippet.length() > 200 ? snippet.substring(0, 200) + "..." : snippet);
            });
        }

        Message saved = messageRepository.save(message);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/reactions")
    public ResponseEntity<?> getAllReactions(HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        return ResponseEntity.ok(reactionRepository.findAll());
    }

    // Toggle: same emoji from the same user on the same message removes it.
    // Returns the full updated reaction list so the polling client can just
    // replace its local copy — simpler than diffing on a REST-polling model.
    @PostMapping("/react")
    public ResponseEntity<?> toggleReaction(@RequestBody Map<String, Object> body,
                                             HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        Long messageId = body.get("messageId") == null ? null : Long.valueOf(body.get("messageId").toString());
        String emoji = (String) body.get("emoji");
        if (messageId == null || emoji == null) return ResponseEntity.badRequest().build();

        Optional<MessageReaction> existing = reactionRepository
                .findByMessageIdAndUsernameAndEmoji(messageId, username, emoji);

        if (existing.isPresent()) {
            reactionRepository.delete(existing.get());
        } else {
            reactionRepository.save(new MessageReaction(messageId, username, emoji));
        }

        return ResponseEntity.ok(reactionRepository.findAll());
    }
}
