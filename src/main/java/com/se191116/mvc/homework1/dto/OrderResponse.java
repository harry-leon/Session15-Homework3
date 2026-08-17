package com.se191116.mvc.homework1.dto;

import com.se191116.mvc.homework1.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String username,
        LocalDateTime createdDate,
        OrderStatus status,
        BigDecimal totalMoney,
        List<OrderItemResponse> items
) {
}
