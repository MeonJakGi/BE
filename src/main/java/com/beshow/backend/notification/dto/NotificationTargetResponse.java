package com.beshow.backend.notification.dto;

import com.beshow.backend.domain.stock.StockStatus;

public record NotificationTargetResponse(
        String screen,
        String skuCode,
        StockStatus status
) {
}
