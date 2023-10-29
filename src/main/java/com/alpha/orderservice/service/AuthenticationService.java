package com.alpha.orderservice.service;

import com.alpha.orderservice.dto.LoginResponse;
import com.alpha.orderservice.input.LoginInput;

public interface AuthenticationService {
    LoginResponse authenticate(LoginInput loginInput);
}
