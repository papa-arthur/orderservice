package com.alpha.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<ProductLine> products;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
