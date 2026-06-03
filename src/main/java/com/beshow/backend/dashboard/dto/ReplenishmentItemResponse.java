package com.beshow.backend.dashboard.dto;

import com.beshow.backend.domain.stock.StockStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReplenishmentItemResponse(
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
        Long shelfId,
        String shelfName,
        Long slotId,
        String slotCode,
        String locationLabel,
        LocalDateTime detectedAt,
        int priority,
        String reasonSummary
) {
}