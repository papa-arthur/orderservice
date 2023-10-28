package com.alpha.orderservice.dto;

import com.alpha.orderservice.entity.ProductLine;
import com.alpha.orderservice.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
public class OrderDto {
    private Long id;
    private List<ProductLineDto> products;
    private UserDto user;
}
