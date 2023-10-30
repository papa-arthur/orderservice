package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.OrderDto;
import com.alpha.orderservice.dto.ProductLineDto;
import com.alpha.orderservice.repository.OrderRepository;
import com.alpha.orderservice.repository.UserRepository;
import jakarta.transaction.Transactional;
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
class OrderControllerTest {

    @Autowired
    private WebGraphQlTester graphQlTester;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    private long userId;
    private long productId;

    @BeforeEach
    void setUp() {
        userId = this.graphQlTester.documentName("createNewUser").execute()
                .path("createUser.id").entity(Long.class).get();
        WebGraphQlTester webGraphQlTester = this.graphQlTester.mutate()
                .headers(httpHeaders -> httpHeaders.setBasicAuth("papa@mail.com", "123"))
                .build();
        productId = webGraphQlTester.documentName("createProduct").execute()
                .path("createProduct.id").entity(Integer.class).get();

    }

    @AfterEach
    @Transactional
    void tearDown() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "fred@mail.com", password = "fred123", roles = "ADMIN")
    void createOrderGivenProductHasEnoughStocksShouldCreateOrder() {
        this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 10
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .path("createOrder.productLines").entityList(ProductLineDto.class).hasSize(1)
                .path("createOrder.productLines[0].productName").entity(String.class).isEqualTo("Vintage Coffee Cup");
    }

    @Test
    @WithMockUser(username = "fred@mail.com", password = "fred123", roles = "ADMIN")
    void createOrderGivenProductDoesNotHaveEnoughStocksShouldCreateOrder() {
        this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 50
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .errors().satisfy((errors) -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                    assertThat(errors.get(0).getMessage()).startsWith("Failed to create order!");
                });
    }

    @Test
    @WithMockUser(username = "fred@mail.com", password = "fred123", roles = "ADMIN")
    void createOrderGivenNegativeQuantityShouldReturnBadRequestError() {
        this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: -50
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .errors().satisfy((errors) -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
                    assertThat(errors.get(0).getMessage()).startsWith("Failed to create order!");
                });
    }

    @Test
    @WithMockUser(username = "fred@mail.com", password = "fred123", roles = "ADMIN")
    void createOrderGivenUserDoesNotExistReturnNotFoundError() {
        this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 50
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, 999, productId)).execute()
                .errors().satisfy((errors) -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
                    assertThat(errors.get(0).getMessage()).isEqualTo(String.format("Failed to create order! User with id '%d' does not exist", 999));
                });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void deleteOrderGivenOrderExistsShouldDelete() {
        long orderId = this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 10
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .path("createOrder.id").entity(Long.class).get();
        this.graphQlTester.document(String.format("""
                mutation{
                    deleteOrder(orderId: %d)
                }
                """, orderId)).executeAndVerify();


    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void deleteOrderGivenOrderDoesNotExistShouldReturnNotFoundError() {

        this.graphQlTester.document(String.format("""
                mutation{
                    deleteOrder(orderId: %d)
                }
                """, 999)).execute().errors().satisfy((errors) -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            assertThat(errors.get(0).getMessage()).isEqualTo(String.format("Deletion failed! Order with id: %d could not be found", 999));
        });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void orders() {
        this.graphQlTester.document(String.format("""
                        mutation{
                             createOrder(order:{
                             userId: %d
                             productLines:[
                               {
                                 productId: %d
                                 quantity: 10
                               }]
                           }){
                             id,productLines{
                               quantity, productId,productName
                             }
                           }
                         }
                """, userId, productId)).executeAndVerify();

        graphQlTester.documentName("orders").execute()
                .path("orders").entityList(OrderDto.class).hasSize(1)
                .path("orders[0].productLines").entityList(ProductLineDto.class).hasSize(1);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getOrderByIdGivenOrderExistReturnOrder() {
        long orderId = this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 10
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .path("createOrder.id").entity(Long.class).get();
        this.graphQlTester.document(String.format("""
                        query {
                             getOrderById(orderId: %d){
                               id,
                               productLines{
                                 id,productId,
                                 quantity,productName
                               }
                             }
                        }
                        """, orderId)).execute()
                .path("getOrderById.id").entity(Long.class).isEqualTo(orderId);
    }


    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getOrderByIdGivenOrderDoesNotExistShouldReturnNotFoundError() {
        int orderId = 999;
        this.graphQlTester.document(String.format("""
                        query {
                             getOrderById(orderId: %d){
                               id,
                               productLines{
                                 id,productId,
                                 quantity,productName
                               }
                             }
                        }
                        """, orderId)).execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
                    assertThat(errors.get(0).getMessage())
                            .isEqualTo(String.format("Order with id: %d could not be found", orderId));
                });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getOrdersForUser() {
        long orderId = this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 10
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .path("createOrder.id").entity(Long.class).get();
        this.graphQlTester.document(String.format("""
                            query {
                            getOrdersForUser(userId:%d){
                                          id,
                                          productLines{
                                            productId,quantity,productName
                                          }
                                        }
                            }
                        """, userId)).execute()
                .path("getOrdersForUser").entityList(OrderDto.class).hasSize(1);
    }


    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getOrdersForUserGivenUserDoesNotExistShouldReturnNotFoundException() {
        long userId = 999;
        this.graphQlTester.document(String.format("""
                    query {
                    getOrdersForUser(userId:%d){
                                  id,
                                  productLines{
                                    productId,quantity,productName
                                  }
                                }
                    }
                """, userId)).execute().errors().satisfy((errors) -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            assertThat(errors.get(0).getMessage()).isEqualTo(String.format("User with id '%d' could not be found", userId));
        });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getProductsForOrderGivenOrderExistShouldReturnOrder() {

        OrderDto order = this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 10
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .path("createOrder").entity(OrderDto.class).get();

        this.graphQlTester.document(String.format("""
                        query{
                             getProductsForOrder(orderId:%d){
                                  id,productId, quantity, productName
                             }
                         }
                        """, order.getId())).execute()
                .path("getProductsForOrder").entityList(ProductLineDto.class).hasSize(1)
                .path("getProductsForOrder[0].productName").entity(String.class).isEqualTo(order.getProductLines().get(0).getProductName());
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getProductsForOrderGivenOrderDoesNotExistShouldReturnNotFoundError() {
        long orderId = 999;

        this.graphQlTester.document(String.format("""
                query{
                     getProductsForOrder(orderId:%d){
                          id,productId, quantity, productName
                     }
                 }
                """, orderId)).execute().errors().satisfy((errors) -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            assertThat(errors.get(0).getMessage()).isEqualTo(String.format("Order with id: %d could not be found", orderId));
        });
    }


    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void updateOrderGivenNewProductLineShouldAddToOrder() {
        OrderDto createdOrder = this.graphQlTester.document(String.format("""
                                mutation{
                                     createOrder(order:{
                                     userId: %d
                                     productLines:[
                                       {
                                         productId: %d
                                         quantity: 10
                                       }]
                                   }){
                                     id,productLines{
                                       quantity, productId,productName
                                     }
                                   }
                                 }
                        """, userId, productId)).execute()
                .path("createOrder").entity(OrderDto.class).get();
        long productLineId = createdOrder.getProductLines().get(0).getId();
        String document = String.format("""
                mutation {
                    updateOrder(order:{
                             orderId: %d
                             productLines:[
                               {
                                 productId: %d
                                 quantity: 2
                               },
                               {
                                id: %d
                                 productId: %d
                                 quantity: 5
                               }
                               ]
                           }){
                             id,productLines{
                               id,quantity, productId, productName
                             }
                           }     
                    }
                """, createdOrder.getId(),this.productId,productLineId, this.productId);

        this.graphQlTester.document(document).execute()
                .path("updateOrder.id").entity(Long.class).isEqualTo(createdOrder.getId())
                .path("updateOrder.productLines").entityList(ProductLineDto.class).hasSize(2);
    }
}