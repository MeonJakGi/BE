package com.beshow.backend.shelfimage.controller;

import com.beshow.backend.global.response.ApiResponse;
import com.beshow.backend.shelfimage.dto.ShelfImageUploadResponse;
import com.beshow.backend.shelfimage.service.ShelfImageService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beshow/shelf-images")
public class ShelfImageController {

    private final ShelfImageService shelfImageService;

    @PostMapping
    public ApiResponse<ShelfImageUploadResponse> uploadShelfImage(
            @RequestParam Long cameraId,
            @RequestPart MultipartFile image,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime capturedAt
    ) {
        ShelfImageUploadResponse response = shelfImageService.uploadShelfImage(
                cameraId,
                image,
                capturedAt
        );

        return ApiResponse.success(response);
    }
}