package com.wellcom.global.socket;

import lombok.Data;

@Data
public class GameDataReqDto {
    private String token;
    private String content;
    private Long roomId;
}
