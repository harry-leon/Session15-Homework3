package com.se191116.mvc.homework1.repository;

import com.se191116.mvc.homework1.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
