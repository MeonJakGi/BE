package com.beshow.backend.analysis.controller;

import com.beshow.backend.analysis.dto.AnalysisCompletedRequest;
import com.beshow.backend.global.response.ApiResponse;
import com.beshow.backend.notification.service.NotificationStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beshow/analysis")
public class AnalysisWebhookController {

    private final NotificationStreamService notificationStreamService;

    @PostMapping("/completed")
    public ApiResponse<Void> analysisCompleted(
            @RequestBody AnalysisCompletedRequest request
    ) {
        if (request.hasAlarm()) {
            notificationStreamService.sendNewNotificationEvent();
        }

        return ApiResponse.success(null);
    }
}