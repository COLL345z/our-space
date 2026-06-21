package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    // Different passwords for each user
    private static final String REHEMA_PASSWORD = "rehema123";
    private static final String COLLINS_PASSWORD = "collins123";

    @GetMapping("/")
    public String loginPage() {
        return "login";   // returns login.html
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session
    ) {
        // Normalize username (case-insensitive, but keep case for display)
        String user = username.trim();
        boolean valid = false;

        if ("Rehema".equalsIgnoreCase(user) && REHEMA_PASSWORD.equals(password)) {
            valid = true;
            user = "Rehema";   // set exact casing
        } else if ("Collins".equalsIgnoreCase(user) && COLLINS_PASSWORD.equals(password)) {
            valid = true;
            user = "Collins";
        }

        if (valid) {
            session.setAttribute("loggedIn", true);
            session.setAttribute("user", user);       // store username in session
            return "redirect:/our-space";
        }

        return "redirect:/?error=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Optional: expose the current user to the frontend
    @GetMapping("/api/current-user")
    @ResponseBody
    public String getCurrentUser(HttpSession session) {
        Object user = session.getAttribute("user");
        return user != null ? user.toString() : "";
    }
}