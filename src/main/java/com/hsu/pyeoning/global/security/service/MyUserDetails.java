package com.hsu.pyeoning.global.security.service;

import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.domain.doctor.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetails implements UserDetailsService {
    private final DoctorRepository doctorRepository;

    @Override
    public UserDetails loadUserByUsername(String doctorLicense) throws UsernameNotFoundException {
        return loadUserByUserLicense(doctorLicense);
    }

    public UserDetails loadUserByUserLicense(String doctorLicense) throws UsernameNotFoundException {
        Doctor doctor = doctorRepository.findById(Long.parseLong(doctorLicense))
                .orElseThrow(() -> new UsernameNotFoundException("해당 의사가 존재하지 않습니다."));

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTOR"));

        return new org.springframework.security.core.userdetails.User(
                doctor.getDoctorLicense().toString(),
                doctor.getDoctorPassword(),
                authorities
        );
    }
}
