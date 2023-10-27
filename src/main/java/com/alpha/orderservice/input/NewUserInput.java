package com.alpha.orderservice.input;

import com.alpha.orderservice.entity.UserRole;
import lombok.Data;

@Data
public class NewUserInput {
    private String name;
    private String email;
    private String password;
    private UserRole role;
}
