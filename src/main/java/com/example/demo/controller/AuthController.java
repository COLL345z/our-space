package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.CurrentUserResolver;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CurrentUserResolver currentUserResolver;
    private final Cloudinary cloudinary;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, 
                           CurrentUserResolver currentUserResolver, Cloudinary cloudinary) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.currentUserResolver = currentUserResolver;
        this.cloudinary = cloudinary;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        var userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty() || !encoder.matches(password, userOpt.get().getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of("token", token, "username", user.getUsername()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        var userOpt = userRepository.findByUsernameIgnoreCase(username);
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("profilePhotoUrl", userOpt.map(User::getProfilePhotoUrl).orElse(null));
        return ResponseEntity.ok(body);
    }

    @PostMapping("/profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        var userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).build();

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "our-space-profiles", "resource_type", "image"));
            String url = uploadResult.get("secure_url").toString();

            User user = userOpt.get();
            user.setProfilePhotoUrl(url);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("profilePhotoUrl", url));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> body,
            HttpServletRequest request, HttpSession session) {
        String username = currentUserResolver.resolve(request, session);
        if (username == null) return ResponseEntity.status(401).build();

        var userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) return ResponseEntity.status(404).build();

        User user = userOpt.get();
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");

        if (!encoder.matches(currentPassword, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "Current password is incorrect"));
        }
        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
