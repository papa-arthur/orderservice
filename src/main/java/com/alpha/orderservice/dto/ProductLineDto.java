package com.alpha.orderservice.dto;

import com.alpha.orderservice.entity.Order;
import lombok.Data;

@Data
public class ProductLineDto {
    private long id;
    private Long productId;
    private Long quantity;
    private String productName;
}
