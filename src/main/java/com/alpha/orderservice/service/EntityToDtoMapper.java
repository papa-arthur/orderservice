package com.alpha.orderservice.service;

import com.alpha.orderservice.dto.OrderDto;
import com.alpha.orderservice.dto.ProductDto;
import com.alpha.orderservice.dto.UserDto;
import com.alpha.orderservice.entity.Order;
import com.alpha.orderservice.entity.Product;
import com.alpha.orderservice.entity.User;
import com.alpha.orderservice.input.UserInput;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityToDtoMapper {

    UserDto entityToDto(User user);
    ProductDto entityToDto(Product product);
    OrderDto entityToDto(Order order);

    List<UserDto> entityToDto(List<User> allUsers);

    void updateFields(@MappingTarget User dbUser, UserInput updateUser);
}
