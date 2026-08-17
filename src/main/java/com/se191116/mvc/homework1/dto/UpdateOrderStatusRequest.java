package com.se191116.mvc.homework1.dto;

import com.se191116.mvc.homework1.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
