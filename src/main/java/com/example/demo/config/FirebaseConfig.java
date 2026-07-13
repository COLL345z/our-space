package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        InputStream serviceAccount;

        String jsonEnv = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
        if (jsonEnv != null && !jsonEnv.isBlank()) {
            serviceAccount = new ByteArrayInputStream(jsonEnv.getBytes(StandardCharsets.UTF_8));
        } else {
            // Local dev fallback — the file you saved at src/main/resources/
            serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}
