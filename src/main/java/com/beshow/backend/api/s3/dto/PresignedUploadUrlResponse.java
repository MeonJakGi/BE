package com.beshow.backend.api.s3.dto;

public record PresignedUploadUrlResponse(
        String folderName,
        String s3Key,
        String uploadUrl
) {
}