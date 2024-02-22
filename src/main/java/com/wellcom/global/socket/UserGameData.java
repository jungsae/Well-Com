package com.wellcom.global.socket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserGameData {
    private String sessionId; // 세션 정보
    private int num; // 입력 값
}
