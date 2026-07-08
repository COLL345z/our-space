package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {
    private final JwtUtil jwtUtil;
    public CurrentUserResolver(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    public String resolve(HttpServletRequest request, HttpSession session) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String username = jwtUtil.validateAndGetUsername(header.substring(7));
            if (username != null) return username;
        }
        Object sessionUser = session.getAttribute("user");
        return sessionUser != null ? sessionUser.toString() : null;
    }
}
