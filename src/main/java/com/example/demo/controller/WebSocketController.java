package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.repository.MessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class WebSocketController {

    private final MessageRepository messageRepository;

    public WebSocketController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public Message sendMessage(@Payload Message message) {
        // Set timestamp if not set
        if (message.getTimestamp() == null) {
            message.setTimestamp(Instant.now().toString());
        }
        
        // Save to database
        Message saved = messageRepository.save(message);
        
        return saved;
    }
}
