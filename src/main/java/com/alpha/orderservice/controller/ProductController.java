package com.alpha.orderservice.controller;

import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.input.NewProductInput;
import com.alpha.orderservice.input.UpdateProductInput;
import com.alpha.orderservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @MutationMapping
    ProductDto createProduct(@Argument(name = "product") NewProductInput newProductInput) {
        return productService.createProduct(newProductInput);
    }

    @MutationMapping
    ProductDto updateProduct(@Argument(name = "product") UpdateProductInput updateProductInput, @Argument long productId) {
        return productService.updateProduct(updateProductInput, productId);
    }

    @MutationMapping
    String deleteProduct(@Argument long productId) {
        return productService.deleteProduct(productId);
    }

    @QueryMapping
    List<ProductDto> products() {
        return productService.getAllProducts();
    }

    @QueryMapping
    ProductDto getProductById(@Argument(name = "id") long productId) {
        return productService.getProductById(productId);
    }
}
