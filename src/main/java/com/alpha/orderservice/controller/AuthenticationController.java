package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.LoginResponse;
import com.alpha.orderservice.input.LoginInput;
import com.alpha.orderservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @MutationMapping
    LoginResponse login(@Argument LoginInput loginInput){
        return authenticationService.authenticate(loginInput);
    }

}
