package com.beshow.backend.api.s3.controller;

import com.beshow.backend.global.response.ApiResponse;
import com.beshow.backend.api.s3.dto.PresignedUploadUrlRequest;
import com.beshow.backend.api.s3.dto.PresignedUploadUrlResponse;
import com.beshow.backend.api.s3.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beshow/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final AWSS3Service s3Service;

    @PostMapping("/presigned")
    public ApiResponse<PresignedUploadUrlResponse> createPresignedUploadUrl(
            @RequestBody PresignedUploadUrlRequest request
    ) {
        return ApiResponse.success(s3Service.createPresignedUploadUrl(request));
    }

}