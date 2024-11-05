package com.hsu.pyeoning.global.security.jwt.filter;

import com.hsu.pyeoning.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

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

        String token = jwtTokenProvider.resolveToken(request);
        System.out.println("token: " + token);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            jwtTokenProvider.setSecurityContext(token);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
