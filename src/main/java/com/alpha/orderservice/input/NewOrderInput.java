package com.alpha.orderservice.input;

import lombok.Data;

import java.util.List;

@Data
public class NewOrderInput {
    private long userId;
    private List<ProductLineInput> productLines;

}
