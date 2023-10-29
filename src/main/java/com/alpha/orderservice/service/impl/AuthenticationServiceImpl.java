package com.alpha.orderservice.service.impl;

import com.alpha.orderservice.dto.LoginResponse;
import com.alpha.orderservice.input.LoginInput;
import com.alpha.orderservice.service.AuthenticationService;
import com.alpha.orderservice.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

   private final AuthenticationManager authenticationManager;
   private final JwtTokenService jwtTokenService;
    @Override
    public LoginResponse authenticate(LoginInput loginInput) throws AuthenticationException {

                    Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInput.getEmail(), loginInput.getPassword())
            );
            return new LoginResponse(jwtTokenService.generateToken(authentication));
    }


}
