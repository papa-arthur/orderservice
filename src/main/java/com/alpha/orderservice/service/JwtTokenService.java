package com.alpha.orderservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;

public interface JwtTokenService {

    String generateToken(Authentication authentication) throws AuthenticationException;

    public String generateToken(String name, Collection<? extends GrantedAuthority> authorities, Instant expiresAt);
}
