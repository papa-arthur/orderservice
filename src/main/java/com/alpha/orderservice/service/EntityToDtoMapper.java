package com.alpha.orderservice.service;

import com.alpha.orderservice.dto.OrderDto;
import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.dto.ProductLineDto;
import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.entity.Order;
import com.alpha.orderservice.entity.Product;
import com.alpha.orderservice.entity.ProductLine;
import com.alpha.orderservice.entity.User;
import com.alpha.orderservice.input.UpdateProductInput;
import com.alpha.orderservice.input.UpdateUserInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityToDtoMapper {
    EntityToDtoMapper INSTANCE = Mappers.getMapper(EntityToDtoMapper.class);

    UserDto entityToDto(User user);

    ProductDto entityToDto(Product product);

    OrderDto entityToDto(Order order);

    @Mapping(target = "productId", expression = "java(productLine.getProduct().getId())")
    @Mapping(target = "productName", expression = "java(productLine.getProduct().getName())")
    ProductLineDto entityToDto(ProductLine productLine);


    default ProductDto productLineToProductDto(ProductLine productLine){
       return INSTANCE.entityToDto(productLine.getProduct());
    }

    List<UserDto> entityToUserDtoList(List<User> allUsers);

    List<ProductDto> productEntityToProductDtoList(List<Product> allProducts);
    List<ProductLineDto> entityToProductLineDtoList(List<ProductLine> productLines);

    @Mapping(target = "password", ignore = true)
    void updateFields(@MappingTarget User dbUser, UpdateUserInput updateUser);

    void updateFields(@MappingTarget Product dbProduct, UpdateProductInput updateNewProductInput);

    List<OrderDto> entityToOrderDtoList(List<Order> orders);
}
