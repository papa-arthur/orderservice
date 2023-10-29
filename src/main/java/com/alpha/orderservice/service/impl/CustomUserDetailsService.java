package com.alpha.orderservice.service.impl;

import com.alpha.orderservice.entity.SecurityUser;
import com.alpha.orderservice.entity.User;
import com.alpha.orderservice.exception.UserNotFoundException;
import com.alpha.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(String.format("User with email '%s' does not exist", email))
        );

        return new SecurityUser(user);
    }
}
