package com.example.demo.service;

import com.example.demo.entity.DeviceToken;
import com.example.demo.repository.DeviceTokenRepository;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    /** Sends a push to every device registered for the given username. */
    public void sendToUser(String username, String title, String body, Map<String, String> data) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUsername(username);
        for (DeviceToken deviceToken : tokens) {
            try {
                Message.Builder messageBuilder = Message.builder()
                    .setToken(deviceToken.getToken())
                    .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());
                
                // Add data payload for better handling in app
                if (data != null && !data.isEmpty()) {
                    messageBuilder.putAllData(data);
                }
                
                // Android-specific config
                messageBuilder.setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                        .setChannelId(getChannelId(data))
                        .setColor("#FFB347")
                        .build())
                    .build());
                
                FirebaseMessaging.getInstance().send(messageBuilder.build());
            } catch (Exception e) {
                System.err.println("Failed to send notification to " + username + ": " + e.getMessage());
            }
        }
    }

    /** Sends to everyone EXCEPT the given username — for "your partner did X" notifications. */
    public void notifyPartner(String actingUsername, String title, String body, Map<String, String> data) {
        List<String> allUsers = List.of("Rehema", "Collins");
        
        for (String user : allUsers) {
            if (!user.equalsIgnoreCase(actingUsername)) {
                sendToUser(user, title, body, data);
            }
        }
    }
    
    /** Specific method for memory notifications */
    public void notifyMemoryAdded(String senderName, String memoryTitle, String memoryCategory) {
        String emoji = switch (memoryCategory) {
            case "LINK" -> "🔗";
            case "TEXT" -> "📝";
            case "GIFT" -> "🎁";
            case "LETTER" -> "💌";
            default -> "📸";
        };
        
        String title = senderName + " " + emoji;
        String body = "Added a new " + memoryCategory.toLowerCase() + ": " + memoryTitle;
        
        Map<String, String> data = Map.of(
            "type", "memory",
            "category", memoryCategory,
            "sender", senderName
        );
        
        notifyPartner(senderName, title, body, data);
    }
    
    /** Specific method for message notifications */
    public void notifyMessageReceived(String senderName, String messagePreview) {
        String title = senderName + " 💬";
        String body = messagePreview;
        
        Map<String, String> data = Map.of(
            "type", "message",
            "sender", senderName
        );
        
        notifyPartner(senderName, title, body, data);
    }
    
    private String getChannelId(Map<String, String> data) {
        if (data == null) return "general";
        
        String type = data.get("type");
        if (type == null) return "general";
        
        return switch (type) {
            case "memory" -> "memories";
            case "message" -> "messages";
            case "letter" -> "letters";
            default -> "general";
        };
    }
}
