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
    public void sendToUser(String username, String title, String body) {
        sendToUser(username, title, body, null);
    }

    /** Sends a push to every device registered for the given username with data. */
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
                
                FirebaseMessaging.getInstance().send(messageBuilder.build());
            } catch (Exception e) {
                System.err.println("Failed to send notification to " + username + ": " + e.getMessage());
            }
        }
    }

    /** Sends to everyone EXCEPT the given username — for "your partner did X" notifications. */
    public void notifyPartner(String actingUsername, String title, String body) {
        notifyPartner(actingUsername, title, body, null);
    }

    /** Sends to everyone EXCEPT the given username with data payload. */
    public void notifyPartner(String actingUsername, String title, String body, Map<String, String> data) {
        List<String> allUsers = List.of("Rehema", "Collins"); // adjust if you ever add more users
        
        for (String user : allUsers) {
            if (!user.equalsIgnoreCase(actingUsername)) {
                sendToUser(user, title, body, data);
            }
        }
    }
}
