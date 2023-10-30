package com.alpha.orderservice.service;

import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.input.UpdateUserInput;
import com.alpha.orderservice.input.NewUserInput;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserInput newUserInput);
    UserDto getUserById(Long userId);
    List<UserDto> getAllUsers();

    UserDto updateUser(UpdateUserInput user);

    String deleteUser(long userId);
}
