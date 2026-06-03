package com.beshow.backend.dashboard.dto;

import java.util.List;

public record OrderListResponse(
        List<OrderItemResponse> items
) {
}
