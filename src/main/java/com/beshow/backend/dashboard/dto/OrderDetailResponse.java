package com.beshow.backend.dashboard.dto;

import java.time.LocalDateTime;

public record OrderDetailResponse(
        String skuCode,
        String productName,
        String productImageUrl,
        String orderStatus,
        String orderStatusLabel,
        OrderInventoryResponse inventory,
        TaskLocationResponse location,
        String reasonSummary,
        LocalDateTime updatedAt
) {
}