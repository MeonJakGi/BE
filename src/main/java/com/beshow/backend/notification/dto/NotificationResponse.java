package com.beshow.backend.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long alarmId,
        Long stockId,
        String alarmType,
        String message,
        String productName,
        String locationLabel,
        boolean isRead,
        LocalDateTime createdAt
) {
}