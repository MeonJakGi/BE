package com.beshow.backend.api.s3.dto;

public record PresignedUploadUrlRequest(
        String folderName,
        String originalFilename,
        String contentType
) {
}