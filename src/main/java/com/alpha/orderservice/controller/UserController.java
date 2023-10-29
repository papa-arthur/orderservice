package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.input.UpdateUserInput;
import com.alpha.orderservice.input.NewUserInput;
import com.alpha.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @MutationMapping
    UserDto createUser(@Argument NewUserInput user) {
        return userService.createUser(user);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    UserDto updateUser(@Argument UpdateUserInput user, @Argument(name = "userId") long userId) {
        return userService.updateUser(user, userId);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    String deleteUser(@Argument(name = "userId") long userId) {
        return userService.deleteUser(userId);
    }

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    List<UserDto> users(){
        return userService.getAllUsers();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    UserDto getUserById(@Argument(name = "id") long userId) {
        return userService.getUserById(userId);
    }





}
