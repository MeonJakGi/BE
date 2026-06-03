package com.beshow.backend.dashboard.dto;

import java.time.LocalDateTime;

public record DashboardSummaryResponse(
        Long storeId,
        String storeName,
        LocalDateTime lastUpdatedAt,
        DashboardSummaryCountResponse summary
) {
}
