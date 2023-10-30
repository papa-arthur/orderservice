package com.alpha.orderservice.input;

import com.alpha.orderservice.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserInput {
    private String name;
    private String email;
    private String password;
    private UserRole role;
}
