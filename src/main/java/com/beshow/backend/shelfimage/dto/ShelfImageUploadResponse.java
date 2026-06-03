package com.beshow.backend.shelfimage.dto;

import java.time.LocalDateTime;

public record ShelfImageUploadResponse(
        Long shelfImageId,
        Long shelfId,
        Long cameraId,
        String imageS3Key,
        String imageUrl,
        Integer imageWidth,
        Integer imageHeight,
        LocalDateTime capturedAt
) {
}