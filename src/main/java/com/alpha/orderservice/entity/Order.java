package com.alpha.orderservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonBackReference
    private List<ProductLine> productLines;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
