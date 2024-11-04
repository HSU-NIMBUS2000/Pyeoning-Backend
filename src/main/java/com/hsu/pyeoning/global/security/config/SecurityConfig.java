package com.hsu.pyeoning.global.security.config;

import com.hsu.pyeoning.global.security.jwt.JwtTokenProvider;
import com.hsu.pyeoning.global.security.jwt.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())
                
                // 세션 정책 설정 (STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 요청 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
                        .requestMatchers(
                                "/api/doctor/registration",
                                "/api/doctor/login",
                                "/api/doctor/checkLicense",
                                "/api/patient/registration",
                                "/api/patient/login"
                        ).permitAll() // 회원가입, 로그인, 면허 중복 확인은 인증 불필요
                        .requestMatchers(
                                "/api/summary/patientSummary/**",
                                "/api/patient/{patientId}/modifyPrompt",
                                "/api/patient/list",
                                "/api/patient/{patientId}/detail",
                                "/api/chat/history/doctor"
                        ).hasRole("DOCTOR") // 의사만 접근 가능
                        .requestMatchers("/api/patient/doctorInfo", "/api/chat/history/patient").hasRole("PATIENT") // 환자만 접근 가능
                        .anyRequest().authenticated() // 그 외 요청은 인증 필요
                )

                // 헤더 설정 (프레임 옵션 동일 출처 허용)
                .headers(headers -> headers
                        .frameOptions().sameOrigin()
                )
                
                // JWT 필터 추가
                .addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
}
