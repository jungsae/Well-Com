package com.wellcom.domain.Member.Controller;

import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.wellcom.domain.Member.Dto.MemberSignUpDto;
import com.wellcom.domain.Member.Role;
import com.wellcom.domain.Member.Service.MemberService;
import com.wellcom.global.auth.jwt.service.JwtService;
import com.wellcom.global.auth.oauth2.CustomOAuth2User;
import com.wellcom.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimAccessor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    @PostMapping("/sign-up")
    public String signUp(@RequestBody MemberSignUpDto memberSignUpDto) throws Exception {
        log.info(memberSignUpDto.toString());
        memberService.signUp(memberSignUpDto);
        return "회원가입 성공";
    }

    @GetMapping("/admin/list")
    @ResponseBody
    public String test(){
        return "test successfully";
    }

    @GetMapping("/members/reissue")
    public void reIssueToken(){
        // reissue용 api 아무것도 실행 안함
    }
}
