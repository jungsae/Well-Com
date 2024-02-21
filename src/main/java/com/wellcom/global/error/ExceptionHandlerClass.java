package com.wellcom.global.error;

import com.nimbusds.oauth2.sdk.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
    /**
     * 자신이 생성하지 않은 SharingRoom에 수정 혹은 삭제시 발생
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> AccessDeniedHandler(AccessDeniedException e){
        log.error("AccessDeniedException message : " + e.getMessage());
        return ErrorResponseDto.makeMessage(HttpStatus.FORBIDDEN, e.getMessage());
    }
    /**
     * 파일 업로드 용량 초과시 발생
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("handleMaxUploadSizeExceededException", e);
        return ErrorResponseDto.makeMessage(HttpStatus.REQUEST_ENTITY_TOO_LARGE, "업로드 할 수 있는 파일의 최대 크기는 10MB입니다");
    }


}
