package com.se191116.mvc.homework1.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal priceBuy,
        BigDecimal lineTotal
) {
}
