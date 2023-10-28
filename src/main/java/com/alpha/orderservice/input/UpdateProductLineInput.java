package com.alpha.orderservice.input;

import lombok.Data;

@Data
public class UpdateProductLineInput {
    private long id;
    private long productId;
    private int quantity;

}
