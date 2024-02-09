package com.wellcom.domain.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseDto {
    public static ResponseEntity<Map<String, Object>> makeMessage(HttpStatus httpStatus, String s){
        Map<String, Object> body = new HashMap<>();
        body.put("status", Integer.toString(httpStatus.value()));
        body.put("status_message", httpStatus.getReasonPhrase());
        body.put("error_message", s);
        return new ResponseEntity<>(body, httpStatus);
    }
}
