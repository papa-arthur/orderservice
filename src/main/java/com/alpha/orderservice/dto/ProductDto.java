package com.alpha.orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductDto {
    private Long id;
    private String name;
    private int stock;
    private double price;
}
