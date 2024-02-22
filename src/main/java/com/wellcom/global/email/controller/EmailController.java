package com.wellcom.global.email.controller;

import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.global.common.CommonResponse;
import com.wellcom.global.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final MemberRepository memberRepository;

    @PostMapping("/send-email")
    public ResponseEntity<CommonResponse> emailConfirm(@RequestParam(value = "email") String email) throws Exception {
        if(memberRepository.findByEmail(email).isPresent()){
            return ResponseEntity.ok(new CommonResponse(HttpStatus.BAD_REQUEST, "중복 이메일 발생", ""));
        };
        String confirm = emailService.sendSimpleMessage(email);

        return ResponseEntity.ok(new CommonResponse(HttpStatus.OK, "메일 발송 성공", confirm));
    }
}