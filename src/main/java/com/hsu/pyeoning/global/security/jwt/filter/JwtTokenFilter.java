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
import org.springframework.web.util.ContentCachingRequestWrapper;

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

        // 요청을 ContentCachingRequestWrapper로 감싸서 여러 번 읽을 수 있게 함
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        // 바디에서 토큰을 확인할 특정 경로들 (세션종료, 요약보고서 생성
        boolean shouldReadBodyForToken = path.equals("/api/chat/endSession") || path.equals("/api/summary/create");
        String token = "";

        // 특정 경로에 대해서만 바디에서 토큰 확인
        if (shouldReadBodyForToken) {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(body);
            JsonNode tokenNode = rootNode.path("token");
            if (!tokenNode.isMissingNode()) {
                token = tokenNode.asText();
            }
        } else {
            // 헤더에서 토큰 확인
            token = jwtTokenProvider.resolveToken(cachingRequest);

        }

        // log
        System.out.println("token: " + token);

        // 3. 토큰 유효성 검증 & SecurityContext에 설정
        if (token != null && jwtTokenProvider.validateToken(token)) {
            jwtTokenProvider.setSecurityContext(token);
        }

        // 다시 요청을 필터 체인으로 보냄
        filterChain.doFilter(cachingRequest, servletResponse);
    }

}
