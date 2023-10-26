package com.alpha.orderservice.service.impl;

import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.entity.User;
import com.alpha.orderservice.exception.UserNotFoundException;
import com.alpha.orderservice.input.UserInput;
import com.alpha.orderservice.repository.UserRepository;
import com.alpha.orderservice.service.EntityToDtoMapper;
import com.alpha.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EntityToDtoMapper mapper;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDto createUser(UserInput userInput) {
        User user = User.builder()
                .name(userInput.getName())
                .email(userInput.getEmail())
                .password(passwordEncoder.encode(userInput.getPassword()))
                .role(userInput.getRole())
                .build();

        return mapper.entityToDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new UserNotFoundException(String.format("User with id '%s' does not exist", userId))
        );
        return mapper.entityToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return mapper.entityToDto(userRepository.findAll());
    }

    @Override
    public UserDto updateUser(UserInput updateUser, long userId) {
        User dbUser = userRepository.findById(userId).orElseThrow(
                ()->new UserNotFoundException(String.format("User with id '%s' does not exist", userId))
        );
        mapper.updateFields(dbUser, updateUser);
        if (updateUser.getPassword() != null){
            dbUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        return mapper.entityToDto(userRepository.save(dbUser));
    }

    @Override
    public String deleteUser(long userId) {
         if (userRepository.existsById(userId)){
             userRepository.deleteById(userId);
             return String.format("User with id '%s' deleted successfully", userId);
         }
         throw new UserNotFoundException(String.format("User with id '%s' does not exist", userId));
    }
}
