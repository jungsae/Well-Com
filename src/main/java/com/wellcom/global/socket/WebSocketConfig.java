package com.wellcom.global.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private final ChatPreHandler chatPreHandler;
    private final ChatErrorHandler chatErrorHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/portfolio").setAllowedOriginPatterns("*").withSockJS();
        registry.setErrorHandler(chatErrorHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/queue", "/topic");

        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatPreHandler);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        MyWebSocketHandler handler = new MyWebSocketHandler();

        registry.addHandler(handler, "/sendMessage")
                .setAllowedOriginPatterns("*");
    }
}

