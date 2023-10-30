package com.alpha.orderservice.service.impl;

import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.entity.Product;
import com.alpha.orderservice.exception.ProductNotFoundException;
import com.alpha.orderservice.input.NewProductInput;
import com.alpha.orderservice.input.UpdateProductInput;
import com.alpha.orderservice.repository.ProductRepository;
import com.alpha.orderservice.service.EntityToDtoMapper;
import com.alpha.orderservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final EntityToDtoMapper mapper;

    @Override
    public ProductDto createProduct(NewProductInput newProductInput) {
        Product newProduct = Product.builder()
                .price(newProductInput.getPrice())
                .stock(newProductInput.getStock())
                .name(newProductInput.getName())
                .build();
        return mapper.entityToDto(productRepository.save(newProduct));
    }

    @Override
    public ProductDto updateProduct(UpdateProductInput updateNewProductInput) {
        Product dbProduct = productRepository.findById(updateNewProductInput.getProductId()).orElseThrow(
                () -> new ProductNotFoundException(String.format("Update failed! Product with id: %s not found", updateNewProductInput.getProductId())));
        mapper.updateFields(dbProduct, updateNewProductInput);
        return mapper.entityToDto(productRepository.save(dbProduct));
    }

    @Override
    public String deleteProduct(long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
            return String.format("Product with id: %d deleted successfully", productId);
        }

        throw new ProductNotFoundException(String.format("Deletion failed! Product with id: %d not found", productId));
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return mapper.productEntityToProductDtoList(productRepository.findAll());
    }

    @Override
    public ProductDto getProductById(long productId) {
        Product dbProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id: %d not found", productId)));
        return mapper.entityToDto(dbProduct);
    }

}
