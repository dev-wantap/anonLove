package com.anonLove.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long userId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    } // 권한 목록 반환

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    } // 계정 만료

    @Override
    public boolean isAccountNonLocked() {
        return true;
    } // 계정 잠금

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    } // 비밀번호 만료

    @Override
    public boolean isEnabled() {
        return true;
    } // 계정 활성화
}