package com.wellcom.domain.Member.Controller;

import com.wellcom.domain.Member.Dto.MemberSignUpDto;
import com.wellcom.domain.Member.Role;
import com.wellcom.domain.Member.Service.MemberService;
import com.wellcom.global.auth.jwt.service.JwtService;
import com.wellcom.global.auth.oauth2.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimAccessor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @GetMapping("/google/token")
    public ResponseEntity<?> signUp(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(), Role.GUEST.name());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
        log.info(accessToken);
        log.info(refreshToken);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/list")
    @ResponseBody
    public String test(){
        return "test successfully";
    }
}
