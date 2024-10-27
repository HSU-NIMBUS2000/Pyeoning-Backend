package com.hsu.pyeoning.domain.role;

import org.springframework.security.core.GrantedAuthority;

public enum RoleName implements GrantedAuthority {
    ROLE_DOCTOR, ROLE_PATIENT;

    @Override
    public String getAuthority() {
        return name();
    }
}
