package com.alpha.orderservice.input;

import lombok.Data;

@Data
public class UpdateUserInput {
    private long id;
    private String name;
    private String email;
    private String password;
}
