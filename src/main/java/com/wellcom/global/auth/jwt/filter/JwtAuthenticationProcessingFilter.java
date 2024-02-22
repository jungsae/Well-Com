package com.wellcom.global.auth.jwt.filter;

import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.domain.Member.Member;
import com.wellcom.global.error.ErrorResponseDto;
import com.wellcom.global.auth.oauth2.util.PasswordUtil;
import com.wellcom.global.auth.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String[] NO_CHECK_URLS = {"/login", "/sign-up", "/send-email"};
    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    /**
     * JWT 인증 필터 처리 과정
     * 1. NO_CHECK_URLS 에 포함되어 있는 API요청의 경우 JWT 인증 필터를 거치지 않고 지나간다.
     * 2. API 요청 시 헤더에 RefreshToken이 있는지 여부를 확인한 후 RefreshToken이 있을 경우 RefreshToken의 만료 체크 및 AccessToken 재발급
     * 3. 그 외의 경우엔 AccessToken이 넘어온 것이므로 JWT parsing 후 유저 인증 과정 진행
    */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            for(String url : NO_CHECK_URLS){
                if (request.getRequestURI().equals(url)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            String refreshToken = jwtService.extractRefreshToken(request)
                    .filter(jwtService::isTokenValid)
                    .orElse(null);

            if (refreshToken != null) {
                checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
                return;
            }
            checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    /**
     * checkRefreshTokenAndReIssueAccessToken() 메소드
     * DB에 저장되어 있는 RefreshToken을 찾아 유저의 정보를 이용해 새로운 AccessToken 발급
     * 메소드에 들어오기 전에 이미 필터에서 RefreshToken 만료 여부를 체크했기 때문에 재발급만 해주면 된다.
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        memberRepository.findByRefreshToken(refreshToken)
                .ifPresent(member -> {
                    try {
                        jwtService.sendAccessToken(response, jwtService.createAccessToken(member.getEmail(), member.getRole().name()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * reIssueRefreshToken() 메소드
     * RefreshToken을 재발급 해주는 메소드
     * RefreshToken 만료 시 에러메세지를 발송해 클라이언트를 로그아웃 처리하기 때문에 사용 안함.
     */
    private String reIssueRefreshToken(Member member) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        member.updateRefreshToken(reIssuedRefreshToken);
        memberRepository.saveAndFlush(member);
        return reIssuedRefreshToken;
    }

    /**
     * checkAccessTokenAndAuthentication() 메소드
     * AccessToken의 만료 체크 및 인증을 처리하는 메소드
     * -> .filter로 토큰의 유효성 검사
     * -> 유효할 경우 토큰의 payload에서 email값을 조회
     * -> 토큰의 email값을 이용해 DB의 유저 정보 조회
     * -> 위의 코드들이 정상적으로 진행되었다면 인증 정보 저장하는 saveAuthentication()메소드 실행
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> {
                    try {
                        jwtService.extractEmail(accessToken)
                                .ifPresent(email -> memberRepository.findByEmail(email)
                                        .ifPresent(this::saveAuthentication));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        filterChain.doFilter(request, response);
    }

    /**
     * saveAuthentication() 메소드
     * 인증 정보를 저장하는 메소드
     * 소셜 로그인을 한 유저의 경우 비밀번호 값을 받아오지 못하기 때문에 passwordUtil을 이용해 자체적으로 랜덤 비밀번호 생성
     * SecurityContextHolder에 인증 정보 저장 (OncePerRequestFilter의 장점)
     */
    public void saveAuthentication(Member curMember) {
        String password = curMember.getPassword();
        if (password == null) {
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(curMember.getEmail())
                .password(password)
                .roles(curMember.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
