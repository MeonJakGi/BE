package com.beshow.backend.dashboard.dto;

import com.beshow.backend.domain.stock.StockStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReplenishmentDetailResponse(
        String skuCode,
        String productName,
        String productImageUrl,
        StockStatus status,
        boolean misplaced,
        String statusLabel,
        int inventoryQuantity,
        int frontQuantity,
        int backQuantity,
        int detectedQuantity,
        Integer estimatedShelfQuantity,
        BigDecimal confidence,
        TaskLocationResponse location,
        LocalDateTime detectedAt,
        String reasonSummary
) {
}