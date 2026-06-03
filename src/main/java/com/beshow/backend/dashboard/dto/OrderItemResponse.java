package com.beshow.backend.dashboard.dto;

import java.time.LocalDateTime;

public record OrderItemResponse(
        String skuCode,
        String productName,
        String status,
        LocalDateTime updatedAt,
        int totalQuantity,
        String shelfLabel,
        String slotLabel,
        boolean isOrderCompleted,
        Integer reorderPoint,
        Integer recommendedOrderQuantity,
        Integer leadTimeDays,
        String statusReason,
        String productImageUrl
) {
}