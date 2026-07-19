package com.example.demo.entity;

import jakarta.persistence.*;

// One row per (message, user, emoji) — a user can react to the same message
// with multiple different emoji, but only once each (toggling re-sends the
// same emoji to remove it, see ChatController.toggleReaction).
@Entity
@Table(name = "message_reactions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"messageId", "username", "emoji"})
})
public class MessageReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String emoji;

    public MessageReaction() {}

    public MessageReaction(Long messageId, String username, String emoji) {
        this.messageId = messageId;
        this.username = username;
        this.emoji = emoji;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
}
