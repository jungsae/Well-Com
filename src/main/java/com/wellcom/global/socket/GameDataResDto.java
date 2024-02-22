package com.wellcom.global.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameDataResDto {
    private String session;
    private String message;
    private Long roomId;
}
