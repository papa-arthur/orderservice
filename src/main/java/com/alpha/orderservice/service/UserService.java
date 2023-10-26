package com.alpha.orderservice.service;

import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.input.UserInput;

import java.util.List;

public interface UserService {

    UserDto createUser(UserInput userInput);
    UserDto getUserById(Long userId);
    List<UserDto> getAllUsers();

    UserDto updateUser(UserInput user, long userId);

    String deleteUser(long userId);
}
