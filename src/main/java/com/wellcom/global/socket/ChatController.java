package com.wellcom.global.socket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/greeting")
    public void enter(ChatMessage message) {
//        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setMessage(message.getSender()+"님이 입장하였습니다.");
//        }
        sendingOperations.convertAndSend("/topic/greeting/"+message.getRoomId(),message);
    }

    @MessageMapping("/sendMessage")
    @SendTo("/greeting")
    public void messageCheck(@Payload String message){
        System.out.println("클라이언트로부터 온 메세지: " + message);
        sendingOperations.convertAndSend("/topic/greeting/",message);
    }
}
