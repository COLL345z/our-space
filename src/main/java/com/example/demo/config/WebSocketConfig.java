package com.example.demo.config;

import com.example.demo.security.StompPrincipal;
import com.example.demo.security.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor authInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(authInterceptor)
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    // Turns the "username" attribute the interceptor stashed
                    // into the STOMP session's actual Principal, so
                    // SimpMessagingTemplate / @MessageMapping methods know
                    // who sent each message without trusting client input.
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                                        Map<String, Object> attributes) {
                        Object username = attributes.get("username");
                        return username != null ? new StompPrincipal(username.toString()) : null;
                    }
                });
        // No SockJS fallback — the Android client speaks raw WebSocket + STOMP
        // directly, SockJS is only needed for browser compatibility.
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");       // server -> clients broadcast destination
        registry.setApplicationDestinationPrefixes("/app"); // client -> server destination
    }
}
