package com.alpha.orderservice.repository;

import com.alpha.orderservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
