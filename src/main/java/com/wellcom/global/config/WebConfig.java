package com.wellcom.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 적용
                .allowedOriginPatterns("*") // 허용할 오리진 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 허용할 헤더
                .allowCredentials(true) // 쿠키 허용
                .maxAge(3600); // pre-flight 요청의 캐시 시간
    }
}