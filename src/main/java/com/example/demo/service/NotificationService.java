package com.example.demo.service;

import com.example.demo.entity.DeviceToken;
import com.example.demo.repository.DeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;  // ← Add this import


@Service
public class NotificationService {

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    /** Sends a push to every device registered for the given username. */
    public void sendToUser(String username, String title, String body) {
        List<DeviceToken> tokens = deviceTokenRepository.findByUsername(username);
        for (DeviceToken deviceToken : tokens) {
            try {
                Message message = Message.builder()
                    .setToken(deviceToken.getToken())
                    .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                    .build();
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                System.err.println("Failed to send notification to " + username + ": " + e.getMessage());
            }
        }
    }

    /** Sends to everyone EXCEPT the given username — for "your partner did X" notifications. */
    public void notifyPartner(String actingUsername, String title, String body) {
        List<String> allUsers = List.of("Rehema", "Collins"); // adjust if you ever add more users
        for (String user : allUsers) {
            if (!user.equalsIgnoreCase(actingUsername)) {
                sendToUser(user, title, body);
            }
        }
    }
}
