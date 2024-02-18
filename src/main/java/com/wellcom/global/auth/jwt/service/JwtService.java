package com.wellcom.global.auth.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.global.error.ErrorResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";
    private static final String BEARER = "Bearer ";

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    /**
     * createAccessToken() 메소드
     * AccessToken 생성하는 메소드 Payload에 email과 role 정보를 넣어서 생성
     */
    public String createAccessToken(String email, String role) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(EMAIL_CLAIM, email)
                .withClaim(ROLE_CLAIM, role)
                .sign(Algorithm.HMAC512(secretKey));
    }
    /**
     * createRefreshToken() 메소드
     * RefreshToken 생성하는 메소드 DB에 저장되어 있어 따로 Payload에 값을 가지고 있지 않아도 됨
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }
    /**
     * sendAccessToken() 메소드
     * Response Body에 AccessToken의 값을 담아 반환해주는 메소드
     * AccessToken이 재발급 될 때 사용
     * 형식 : { "Authorizaton" : {token} }
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Map<String, String> token = new HashMap<>();
        token.put(accessHeader, accessToken);

        String result = objectMapper.writeValueAsString(token);
        response.getWriter().write(result);
        log.info("재발급된 Access Token : {}", accessToken);
    }
    /**
     * sendAccessAndRefreshToken() 메소드
     * 초기 로그인 성공 시 Response Body에 두 토큰의 값을 담아 반환해주는 메소드
     * 형식 : { "Authorization" : {AccessToken}, "AuthorizationRefresh" : {RefreshToken} }
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Map<String, String> token = new HashMap<>();
        token.put(accessHeader, accessToken);
        token.put(refreshHeader, refreshToken);

        String result = objectMapper.writeValueAsString(token);
        response.getWriter().write(result);

        log.info("Access Token, Refresh Token 바디 설정 완료");
    }
    /**
     * extractRefreshToken() 메소드
     * 헤더에 담겨져 온 RefreshToken의 값이 DB에 있는지 확인하는 메소드
     * 헤더에 담겨져 올 때 앞에 "Bearer "이 붙어서 오기 때문에 제거해주는 스트림 사용
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }
    /**
     * extractAccessToken() 메소드
     * 헤더에 담겨져 온 AccessToken의 값이 DB에 있는지 확인하는 메소드
     * 헤더에 담겨져 올 때 앞에 "Bearer "이 붙어서 오기 때문에 제거해주는 스트림 사용
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }
    /**
     * extractEmail() 메소드
     * 기존 JWT 토큰이 HS512 인코딩 방식을 사용했기 때문에 같은 방식으로 디코딩 후 DB에 있는 email과 일치 여부를 확인하는 메소드
     * 에러 발생 시 401 UNAUTHORIZED 에러 발생
     */
    public Optional<String> extractEmail(String accessToken) throws IOException {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(EMAIL_CLAIM)
                    .asString());
        } catch (Exception e) {
            throw new IllegalArgumentException("존재하지 않는 이메일입니다.");
        }
    }
    /**
     * extractRole() 메소드
     * 기존 JWT 토큰이 HS512 인코딩 방식을 사용했기 때문에 같은 방식으로 디코딩 후 DB에 있는 role과 일치 여부를 확인하는 메소드
     * 에러 발생 시 401 UNAUTHORIZED 에러 발생
     */
    public Optional<String> extractRole(String accessToken, HttpServletResponse response) throws IOException{
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(ROLE_CLAIM)
                    .asString());
        } catch (Exception e) {
            throw new IllegalArgumentException("액세스 토큰이 유효하지 않습니다.");
        }
    }
    /**
     * updateRefreshToken() 메소드
     * 생성된 RefreshToken을 해당 유저의 DB에 추가하는 메소드
     * 구글 로그인 성공시 사용되기 때문에 따로 예외처리 X
     */
    public void updateRefreshToken(String email, String refreshToken) {
        try{
            memberRepository.findByEmail(email)
                    .ifPresent(user -> user.updateRefreshToken(refreshToken));
        } catch (Exception e){
            throw new IllegalArgumentException("일치하는 회원이 없습니다.");
        }
    }
    /**
     * isTokenValid() 메소드
     * 헤더에 담겨져온 토큰을 HS512 인증 방식을 통해 디코딩 후 유효성 확인하는 메소드
     */
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
