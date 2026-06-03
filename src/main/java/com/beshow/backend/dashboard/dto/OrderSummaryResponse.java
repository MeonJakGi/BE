package com.beshow.backend.dashboard.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryResponse(
        int orderRequiredSkuCount,
        int soldOutSkuCount,
        LocalDateTime lastUpdatedAt,
        List<CategoryDistributionResponse> categoryDistribution
) {
}