package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.input.UpdateUserInput;
import com.alpha.orderservice.input.NewUserInput;
import com.alpha.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @MutationMapping
    UserDto createUser(@Argument NewUserInput user) {
        return userService.createUser(user);
    }

    @MutationMapping
    UserDto updateUser(@Argument UpdateUserInput user, @Argument(name = "userId") long userId) {
        return userService.updateUser(user, userId);
    }

    @MutationMapping
    String deleteUser(@Argument(name = "userId") long userId) {
        return userService.deleteUser(userId);

    }

    @QueryMapping
    List<UserDto> users(){
        return userService.getAllUsers();
    }

    @QueryMapping
    UserDto getUserById(@Argument(name = "id") long userId) {
        return userService.getUserById(userId);
    }





}
