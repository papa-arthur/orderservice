package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.repository.ProductRepository;
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
class ProductControllerTest {

    @Autowired
    private WebGraphQlTester graphQlTester;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void createProductGivenAdminRoleShouldCreateProduct() {
        this.graphQlTester.documentName("createProduct").execute()
                .path("createProduct.name").entity(String.class).isEqualTo("Vintage Coffee Cup")
                .path("createProduct.price").entity(Double.class).isEqualTo(126.00d);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "USER")
    void createProductGivenUserRoleShouldReturnUnauthorizedError() {
        this.graphQlTester.documentName("createProduct").execute()
                .errors().satisfy((errors) -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.FORBIDDEN);
                });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void updateProductGivenAdminRoleSAndProductExistShouldUpdate() {
        long productId = this.graphQlTester.documentName("createProduct").execute()
                .path("createProduct.id").entity(Long.class).get();

        this.graphQlTester.document(String.format("""
                            mutation{
                                updateProduct(product: {
                                    productId: %d
                                    stock: 50
                                }){
                                    id, name, stock,price
                                }
                            }
                        """, productId)).execute()
                .path("updateProduct.stock").entity(Integer.class).isEqualTo(50);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void updateProductGivenProductDoesNotExist() {
        this.graphQlTester.document(String.format("""
                    mutation{
                        updateProduct(product: {
                            productId: %d
                            stock: 50
                        }){
                            id, name, stock,price
                        }
                    }
                """, 999)).execute().errors().satisfy((errors) -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            assertThat(errors.get(0).getMessage()).isEqualTo(String.format("Update failed! Product with id: %s not found", 999));
        });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void deleteProductGivenAdminRoleAndProductExistsShouldDeleteProduct() {
        long productId = this.graphQlTester.documentName("createProduct").execute()
                .path("createProduct.id").entity(Long.class).get();
        this.graphQlTester.document(String.format("""
                        mutation{
                            deleteProduct(productId: %d)
                        }
                        """, productId)).execute()
                .path("deleteProduct").entity(String.class).isEqualTo(String.format("Product with id: %d deleted successfully", productId));
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void deleteProductGivenAdminRoleAndProductDoesNotExistShouldReturnNotFoundExist() {
        this.graphQlTester.document(String.format("""
                mutation{
                    deleteProduct(productId: %d)
                }
                """, 999)).execute().errors().satisfy((errors) -> {
            assertThat(errors).hasSize(1);
            assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
            assertThat(errors.get(0).getMessage()).isEqualTo(String.format("Deletion failed! Product with id: %d not found", 999));
        });
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void productsGiveProductsExistInDbReturnProductsList() {
        this.graphQlTester.documentName("createProduct").executeAndVerify();

        this.graphQlTester.documentName("products").execute()
                .path("products").entityList(ProductDto.class).hasSize(1)
                .path("products[0].name").entity(String.class).isEqualTo("Vintage Coffee Cup")
                .path("products[0].price").entity(Double.class).isEqualTo(126.00d);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "USER")
    void productsGiveNOProductExistInDbReturnEmptyList() {
        this.graphQlTester.documentName("products").execute()
                .path("products").entityList(ProductDto.class).hasSize(0);
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "ADMIN")
    void getProductById() {
        long productId = this.graphQlTester.documentName("createProduct").execute()
                .path("createProduct.id").entity(Long.class).get();

        this.graphQlTester.document(String.format("""
                        query {
                            getProductById(id: %d){
                                id,name,stock,price
                            }
                        }
                         """, productId)).execute()
                .path("getProductById.id").entity(Long.class).isEqualTo(productId)
                .path("getProductById.name").entity(String.class).isEqualTo("Vintage Coffee Cup");
    }

    @Test
    @WithMockUser(username = "admin@mail.com", password = "admin123", roles = "USER")
    void getProductByIdGivenProductDoesNotExistShouldReturnNotFoundError() {
        this.graphQlTester.document(String.format("""
                        query {
                            getProductById(id: %d){
                                id,name,stock,price
                            }
                        }
                         """, 999)).execute()
                .errors().satisfy((errors) -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getMessage()).isEqualTo(String.format("Product with id: %d not found", 999));
                    assertThat(errors.get(0).getErrorType()).isEqualTo(ErrorType.NOT_FOUND);
                });
    }
}