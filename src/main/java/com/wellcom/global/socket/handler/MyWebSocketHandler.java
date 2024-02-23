package com.wellcom.global.socket.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 텍스트 메시지를 받았을 때의 동작을 정의합니다.
        String payload = message.getPayload();
        System.out.println("Received message from client: " + payload);

        // 받은 메시지에 따른 로직 처리를 수행합니다.
        // 예를 들어, 받은 메시지에 따라 응답을 보낼 수 있습니다.
        String response = "Server response: " + payload;
        session.sendMessage(new TextMessage(response));
    }
}
