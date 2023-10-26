package com.alpha.orderservice.repository;

import com.alpha.orderservice.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {
}
