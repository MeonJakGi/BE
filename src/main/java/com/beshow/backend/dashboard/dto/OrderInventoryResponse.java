package com.beshow.backend.dashboard.dto;

public record OrderInventoryResponse(
        int totalQuantity,
        int reorderPoint,
        int recommendedOrderQuantity,
        Integer leadTimeDays,
        boolean isorderCompleted
) {
}
