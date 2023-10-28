package com.alpha.orderservice.dto;

import com.alpha.orderservice.entity.Order;
import com.alpha.orderservice.entity.UserRole;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.springframework.data.domain.jaxb.SpringDataJaxb;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
