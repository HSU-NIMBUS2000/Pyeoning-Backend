package com.hsu.pyeoning.global.security.jwt.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.pyeoning.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 등록 및 로그인 경로를 필터링하지 않음
        String path = request.getRequestURI();
        if (path.equals("/api/doctor/registration") || path.equals("/api/doctor/login") || path.equals("/api/patient/login")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 1. 헤더에서 토큰 확인
        String token = jwtTokenProvider.resolveToken(request);

        // 2. 헤더에 토큰이 없으면 바디에서 토큰을 확인
        if (token == null) {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

            // "token": "Bearer "로 시작하는 토큰 값 추출
            if (body.contains("\"token\": \"Bearer ")) {
                int startIndex = body.indexOf("\"token\": \"Bearer ") + 10; // "token": " 이후 인덱스
                int endIndex = body.indexOf("\"", startIndex);
                if (endIndex > startIndex) {
                    token = body.substring(startIndex, endIndex);
                }
            }
        }

        // log
        System.out.println("token: " + token);

        // 3. 토큰 유효성 검증 & SecurityContext에 설정
        if (token != null && jwtTokenProvider.validateToken(token)) {
            jwtTokenProvider.setSecurityContext(token);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
