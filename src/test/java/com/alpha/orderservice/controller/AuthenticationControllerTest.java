package com.alpha.orderservice.controller;

import com.alpha.orderservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.WebGraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
class AuthenticationControllerTest {

    @Autowired
    private WebGraphQlTester graphQlTester;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp(){
        this.graphQlTester.documentName("createNewUser").executeAndVerify();

    }

    @AfterEach
    public void clearUserTable() {
        userRepository.deleteAll();
    }


    @Test
    void loginGivenValidCredentialsShouldGetToken() {
        graphQlTester.documentName("login").execute()
                .path("login.token").entity(String.class);
    }


    @Test
    void loginGivenInValidCredentialsShouldReturnUnauthorizedError() {
        graphQlTester.documentName("updateUser").execute()
                .errors().satisfy((errors) -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.UNAUTHORIZED);
                    assertThat(errors.get(0).getMessage()).isEqualTo("Unauthorized");
                });
    }
}