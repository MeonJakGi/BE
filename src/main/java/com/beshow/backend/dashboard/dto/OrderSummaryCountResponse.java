package com.beshow.backend.dashboard.dto;

import java.util.List;

public record OrderSummaryCountResponse(
        int orderNeededCount,
        int outOfInventoryCount,
        List<CategoryDistributionResponse> categoryDistribution
) {
}
