package com.beshow.backend.dashboard.dto;

public record CategoryDistributionResponse(
        String category,
        int count
) {
}