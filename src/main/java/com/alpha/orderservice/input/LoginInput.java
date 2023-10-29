package com.alpha.orderservice.input;

import lombok.Data;

@Data
public class LoginInput {
    private String email;
    private String password;
}
