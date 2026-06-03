package com.beshow.backend.notification.dto;

public record ReadNotificationResponse(
        Long notificationId,
        boolean isRead
) {
}
