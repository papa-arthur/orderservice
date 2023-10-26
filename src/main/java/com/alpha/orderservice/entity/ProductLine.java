package com.alpha.orderservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductLine {
    @Id
    private Long productId;
    private Long quantity;
    @ManyToOne
    @JoinColumn(name="orderId")
    private Order order;
}
