package com.wellcom.global.socket;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;
    private String message;
    private Long roomId;
}
