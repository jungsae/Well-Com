package com.wellcom.global.auth.oauth2.handler;

import com.wellcom.domain.Member.Member;
import com.wellcom.domain.Member.Role;
import com.wellcom.domain.Member.Repository.MemberRepository;
import com.wellcom.global.auth.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.wellcom.global.auth.jwt.service.JwtService;
import com.wellcom.global.auth.oauth2.CustomOAuth2User;
import com.wellcom.global.auth.oauth2.util.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.Cookie;

import static com.wellcom.global.auth.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
//@Transactional
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            String targetUrl = determineTargetUrl(request, response, authentication);

            if(oAuth2User.getRole() == Role.GUEST) {
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(), Role.GUEST.name());
                String refreshToken = jwtService.createRefreshToken();

                jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
                Member findMember = memberRepository.findByEmail(oAuth2User.getEmail())
                                .orElseThrow(() -> {
                                    clearAuthenticationAttributes(request, response);
                                    return new IllegalArgumentException("이메일에 해당하는 유저가 없습니다.");
                                });
                findMember.authorizeUser();

                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
                targetUrl = UriComponentsBuilder.fromUriString(targetUrl).queryParam("accessToken", accessToken).queryParam("refreshToken", refreshToken).build().toUriString();
            } else {
                loginSuccess(response, oAuth2User);
            }
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            throw e;
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(), oAuth2User.getRole().name());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUrl.orElse(getDefaultTargetUrl());

        return UriComponentsBuilder.fromUriString(targetUrl).toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
