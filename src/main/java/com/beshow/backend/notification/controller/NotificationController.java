package com.beshow.backend.notification.controller;

import com.beshow.backend.global.response.ApiResponse;
import com.beshow.backend.notification.dto.NotificationListResponse;
import com.beshow.backend.notification.dto.ReadNotificationResponse;
import com.beshow.backend.notification.dto.UnreadCountResponse;
import com.beshow.backend.notification.service.NotificationService;
import com.beshow.backend.notification.service.NotificationStreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beshow/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationStreamService notificationStreamService;

    @GetMapping("/stream")
    public SseEmitter stream() {
        return notificationStreamService.subscribe();
    }

    @GetMapping
    public ApiResponse<NotificationListResponse> getNotifications() {
        return ApiResponse.success(notificationService.getNotifications());
    }

    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount() {
        return ApiResponse.success(notificationService.getUnreadCount());
    }

    @PatchMapping("/{alarmId}/read")
    public ApiResponse<ReadNotificationResponse> markAsRead(@PathVariable Long alarmId) {
        return ApiResponse.success(notificationService.markAsRead(alarmId));
    }
}