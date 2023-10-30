package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.entity.UserRole;
import com.alpha.orderservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureHttpGraphQlTester
class UserControllerTest {

    @Autowired
    private WebGraphQlTester graphQlTester;
    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createUserCanCreateUser() {
        this.graphQlTester.documentName("createNewUser")
                .execute()
                .path("createUser.name").entity(String.class).isEqualTo("papa");
    }

    @Test
    void createUserGivenUserExistThrowsError() {
        this.graphQlTester.documentName("createExistingUser")
                .execute();
        this.graphQlTester.documentName("createExistingUser")
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void updateUserGivenUserExistAndRequestIsAuthenticated() {
        long userId = this.graphQlTester.documentName("createNewUser").execute()
                .path("createUser.id").entity(Long.class).get();

        graphQlTester.document(String.format("""
                        mutation {
                            updateUser(user: {
                                id: %d
                                name: "Fred"
                                password: "123"
                            }){
                                id, name, email, role
                            }
                        }
                        """, userId))
                .execute()
                .path("updateUser.name").entity(String.class).isEqualTo("Fred")
                .path("updateUser.role").entity(UserRole.class).get();
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void updateUserGivenUserDoesNotExistThrowError() {

        graphQlTester.document(String.format("""
                        mutation {
                            updateUser(user: {
                                id: %d
                                name: "Fred"
                                password: "123"
                            }){
                                id, name, email, role
                            }
                        }
                        """, 999))
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
                    assertThat(errors.get(0).getMessage())
                            .isEqualTo(String.format("User with id '%d' does not exist", 999));
                });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void deleteUserGivenUserExistShouldSucceed() {
        long userId = this.graphQlTester.documentName("createNewUser").execute()
                .path("createUser.id").entity(Long.class).get();

        this.graphQlTester.document(String.format("""
                        mutation{
                        deleteUser(userId:%d)
                        }
                        """, userId)).execute()
                .path("deleteUser").entity(String.class).isEqualTo(String.format("User with id '%s' deleted successfully", userId));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void deleteUserGivenUserDoesNotExistShouldSucceed() {
        this.graphQlTester.document(String.format("""
                        mutation{
                        deleteUser(userId:%d)
                        }
                        """, 999)).execute()
                .errors().satisfy((error) -> {
                    assertThat(error).hasSize(1);
                    assertThat(error.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
                    assertThat(error.get(0).getMessage()).isEqualTo(String.format(" Deletion failed! User with id '%d' does not exist", 999));
                });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "USER")
    void deleteUserGivenUserIsNotAdminShouldThrowForbiddenError() {
        this.graphQlTester.document(String.format("""
                        mutation{
                        deleteUser(userId:%d)
                        }
                        """, 999)).execute()
                .errors().satisfy((error) -> {
                    assertThat(error).hasSize(1);
                    assertThat(error.get(0).getErrorType()).isEqualTo(ErrorType.FORBIDDEN);
                    assertThat(error.get(0).getMessage()).isEqualTo("Forbidden");
                });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void users() {
        this.graphQlTester.documentName("createAdminUser").executeAndVerify();
        this.graphQlTester.documentName("createNewUser").executeAndVerify();
        this.graphQlTester.documentName("createUser").executeAndVerify();

        this.graphQlTester.documentName("users").execute()
                .path("users").entityList(UserDto.class).hasSize(3)
                .path("users[0].name").entity(String.class).isEqualTo("admin");
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getUserByIdGivenUserExistsShouldReturnUser() {
        long userId = this.graphQlTester.documentName("createNewUser").execute()
                .path("createUser.id").entity(Long.class).get();

        graphQlTester.document(String.format("""
                        query GetUserByID{
                              getUserById(id:%d){
                                  name,id,email,role
                              }
                          }
                        """, userId))
                .execute()
                .path("getUserById.name").entity(String.class).isEqualTo("papa")
                .path("getUserById.role").entity(UserRole.class).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getUserByIdGivenUserDoesNotExistsShouldReturnNotFoundError() {

        graphQlTester.document(String.format("""
                        query GetUserByID{
                              getUserById(id:%d){
                                  name,id,email,role
                              }
                          }
                        """, 999))
                .execute()
                .errors().satisfy((errors) ->{
                    assertThat(errors.size()).isEqualTo(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
                    assertThat(errors.get(0).getMessage()).isEqualTo(String.format("User with id '%d' does not exist", 999));
                });
    }
}