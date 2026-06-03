package com.beshow.backend.dashboard.dto;

import java.util.List;

public record ReplenishmentListResponse(
        List<ReplenishmentItemResponse> items
) {
}
