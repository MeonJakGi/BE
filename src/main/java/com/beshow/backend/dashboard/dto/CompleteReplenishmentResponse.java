package com.beshow.backend.dashboard.dto;

import com.beshow.backend.domain.stock.StockStatus;
import java.time.LocalDateTime;

public record CompleteReplenishmentResponse(
        String skuCode,
        StockStatus status,
        String statusLabel,
        LocalDateTime completedAt
) {
}
