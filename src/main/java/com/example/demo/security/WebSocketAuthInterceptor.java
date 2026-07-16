package com.example.demo.security;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Validates the Authorization: Bearer <token> header during the WebSocket
 * handshake, same JwtUtil used by every REST endpoint. If missing/invalid,
 * the handshake is rejected outright (returns false) rather than allowing
 * an anonymous connection.
 *
 * The Android client must send this header on the initial HTTP upgrade
 * request — OkHttp's WebSocket.Builder supports this via
 * Request.Builder().addHeader("Authorization", "Bearer $token") before
 * calling newWebSocket().
 */
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String header = servletRequest.getServletRequest().getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String username = jwtUtil.validateAndGetUsername(header.substring(7));
                if (username != null) {
                    attributes.put("username", username);
                    return true;
                }
            }
        }
        return false; // reject the handshake — no valid token, no connection
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
