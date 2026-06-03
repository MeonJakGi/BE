package com.beshow.backend.dashboard.dto;

public record DashboardSummaryCountResponse(
        int enoughCount,
        int needRefillCount,
        int needCheckCount // stock.isMisplaced() == true
) {
}