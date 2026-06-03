package com.beshow.backend.dashboard.dto;

import java.time.LocalDateTime;

public record CompleteOrderResponse(
        String skuCode,
        boolean isOrderCompleted,
        LocalDateTime orderCompletedAt
) {
}
