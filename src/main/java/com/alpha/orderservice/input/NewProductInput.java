package com.alpha.orderservice.input;

import lombok.Data;

@Data
public class NewProductInput {
    private String name;
    private int stock;
    private double price;
}
