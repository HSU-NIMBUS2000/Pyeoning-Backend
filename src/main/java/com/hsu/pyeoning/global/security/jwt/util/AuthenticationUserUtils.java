package com.hsu.pyeoning.global.security.jwt.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUserUtils {
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println((String) authentication.getPrincipal());
        return (String) authentication.getPrincipal();
    }
}
