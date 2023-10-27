package com.alpha.orderservice.input;

import lombok.Data;

@Data
public class UpdateProductInput {
    private String name;
    private Integer stock;
    private Double price;
}
