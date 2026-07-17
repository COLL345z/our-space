package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDto;
import com.example.demo.entity.Message;
import com.example.demo.repository.MessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class ChatController {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(MessageRepository messageRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // Client sends to /app/chat.send. Sender identity comes from the
    // handshake-authenticated Principal (see WebSocketConfig), NEVER from
    // the payload itself — a client can't spoof who a message is "from".
    // @MessageMapping("/chat.send")
    // public void sendMessage(@Payload ChatMessageDto dto, Principal principal) {
    //     if (principal == null || dto.getContent() == null || dto.getContent().isBlank()) return;

    //     Message message = new Message(principal.getName(), dto.getContent(), Instant.now().toString());
    //     Message saved = messageRepository.save(message);

    //     messagingTemplate.convertAndSend("/topic/messages", saved);
    // }
}
