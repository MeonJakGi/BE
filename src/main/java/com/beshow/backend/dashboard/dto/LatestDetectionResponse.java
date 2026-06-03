package com.beshow.backend.dashboard.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LatestDetectionResponse(
        Long shelfId,
        Long shelfImageId,
        String imageS3Key,
        String imageUrl,
        Integer imageWidth,
        Integer imageHeight,
        LocalDateTime capturedAt,
        String frontEdgePoints,
        List<DetectionOverlayResponse> detections
) {
}
