package com.beshow.backend.analysis.dto;

import java.time.LocalDateTime;

public record AnalysisCompletedRequest(
        Long shelfImageId,
        boolean hasAlarm,
        int createdAlarmCount,
        LocalDateTime completedAt
) {
}