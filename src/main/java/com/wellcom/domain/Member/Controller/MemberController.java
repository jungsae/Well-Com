package com.wellcom.domain.Member.Controller;

import com.wellcom.domain.Member.Dto.MemberSignUpDto;
import com.wellcom.domain.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/sign-up")
    @CrossOrigin(originPatterns = "*")
    public String signUp(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        log.info(memberSignUpDto.toString());
        memberService.signUp(memberSignUpDto);
        return "회원가입 성공";
    }


}
