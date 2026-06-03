package com.beshow.backend.dashboard.dto;

public record TaskLocationResponse(
        Long shelfId,
        String shelfName,
        Long slotId,
        String slotCode,
        String locationLabel
) {
}
