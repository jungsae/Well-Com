package com.wellcom.global.socket;

import com.wellcom.global.auth.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@RequiredArgsConstructor
@Component
public class ChatPreHandler implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "[Bearer ";
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        String authorizationHeader = String.valueOf(accessor.getNativeHeader("Authorization"));

        if(authorizationHeader == null || authorizationHeader.equals("null")){
            throw new MessageDeliveryException("메세지 예외");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        token = token.replace("]", "");
        if(jwtService.isTokenValid(token)){
            return message;
        } else {
            throw new MessageDeliveryException("메세지 에러");
        }
    }
}
