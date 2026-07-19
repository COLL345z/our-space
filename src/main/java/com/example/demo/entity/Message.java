package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String senderUsername;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private String timestamp; // ISO-8601 string, set server-side on receipt

    // NEW: reply support. Denormalized (sender + snippet stored directly on
    // the reply row, not just the id) so rendering a quoted preview never
    // needs a second lookup — same tradeoff WhatsApp/Telegram make.
    private Long replyToId;
    private String replyToSenderUsername;
    private String replyToContent;

    public Message() {}

    public Message(String senderUsername, String content, String timestamp) {
        this.senderUsername = senderUsername;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    public String getReplyToSenderUsername() { return replyToSenderUsername; }
    public void setReplyToSenderUsername(String replyToSenderUsername) { this.replyToSenderUsername = replyToSenderUsername; }
    public String getReplyToContent() { return replyToContent; }
    public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }
}
