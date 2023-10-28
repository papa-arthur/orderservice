package com.alpha.orderservice.service;

import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.input.NewProductInput;
import com.alpha.orderservice.input.UpdateProductInput;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(NewProductInput newProductInput);
    ProductDto updateProduct(UpdateProductInput newProductInput, long productId);
    String deleteProduct(long productId);

    List<ProductDto> getAllProducts();

    ProductDto getProductById(long productId);

}
