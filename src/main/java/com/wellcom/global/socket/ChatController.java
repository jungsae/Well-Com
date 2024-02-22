package com.wellcom.global.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessageSendingOperations sendingOperations;


    /*
    클라이언트가 값을 보내면 들어오는 메소드
     */
    @MessageMapping("/sendMessage")
    public void messageCheck(@Payload GameDataReqDto req, SimpMessageHeaderAccessor accessor) {
        System.out.println(req);
        System.out.println(accessor.getSessionId());

        // 클라이언트에게 메시지 전송
        GameDataResDto res = new GameDataResDto(accessor.getSessionId(), req.getContent(), req.getRoomId());
//        Message message = new Message(req.getContent() + "을(를) 받았습니다");
//        sendingOperations.convertAndSend(accessor.getSessionId(), "/topic/sharing/" + req.getRoomId(), message);
        sendingOperations.convertAndSend("/topic/sharing/" + req.getRoomId(), res);
    }
}