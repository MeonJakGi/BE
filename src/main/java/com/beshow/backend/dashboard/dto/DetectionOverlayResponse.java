package com.beshow.backend.dashboard.dto;

import com.beshow.backend.domain.detection.DepthPosition;
import com.beshow.backend.domain.stock.StockStatus;
import java.math.BigDecimal;

public record DetectionOverlayResponse(
        Long detectionResultId,
        String skuCode,
        String productName,
        String slotCode,
        BboxResponse bbox,
        DepthPosition depthPosition,
        BigDecimal confidence,
        StockStatus status,
        String statusLabel
) {
}
