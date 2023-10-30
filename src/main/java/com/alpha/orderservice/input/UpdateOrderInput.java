package com.alpha.orderservice.input;

import lombok.Data;

import java.util.List;

@Data
public class UpdateOrderInput {
    private long orderId;
    private List<UpdateProductLineInput> productLines;
}
