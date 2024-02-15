package com.wellcom.global.error;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerClass {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> entityNotFoundHandler(EntityNotFoundException e){
        log.error("EntityNotFoundException message : " + e.getMessage());
        return ErrorResponseDto.makeMessage(HttpStatus.NOT_FOUND, e.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> IllegalArgHandler(IllegalArgumentException e){
        log.error("IllegalArgumentException message : " + e.getMessage());
        return ErrorResponseDto.makeMessage(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(ConstraintViolationException.class) //JPA Column이 Unique인 값에 중복으로 넣을때 나오는 에러
    public ResponseEntity<Map<String, Object>> ConstraintViolationExceptionHandler(ConstraintViolationException e){
        log.error("ConstraintViolationException message : " + e.getMessage());
        return ErrorResponseDto.makeMessage(HttpStatus.CONFLICT, e.getMessage());
    }
}
