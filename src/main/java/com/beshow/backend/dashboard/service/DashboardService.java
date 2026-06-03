package com.beshow.backend.dashboard.service;

import com.beshow.backend.dashboard.dto.DashboardSummaryResponse;
import com.beshow.backend.dashboard.dto.LatestDetectionResponse;
import com.beshow.backend.dashboard.dto.OrderDetailResponse;
import com.beshow.backend.dashboard.dto.OrderListResponse;
import com.beshow.backend.dashboard.dto.OrderSummaryResponse;
import com.beshow.backend.dashboard.dto.ReplenishmentDetailResponse;
import com.beshow.backend.dashboard.dto.ReplenishmentListResponse;
import com.beshow.backend.domain.stock.StockStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ReplenishmentDashboardService replenishmentDashboardService;
    private final OrderDashboardService orderDashboardService;
    private final ShelfDashboardService shelfDashboardService;

    public DashboardSummaryResponse getReplenishmentSummary(Long storeId, Long shelfId) {
        return replenishmentDashboardService.getReplenishmentSummary(storeId, shelfId);
    }

    public ReplenishmentListResponse getReplenishmentItems(
            Long storeId,
            Long shelfId,
            StockStatus status,
            Boolean misplaced
    ) {
        return replenishmentDashboardService.getReplenishmentItems(storeId, shelfId, status, misplaced);
    }

    public LatestDetectionResponse getShelfImage(
            Long shelfId,
            StockStatus status,
            Boolean misplaced
    ) {
        return shelfDashboardService.getLatestDetection(shelfId, status, misplaced);
    }

    public ReplenishmentDetailResponse getReplenishmentItemDetail(String sku) {
        return replenishmentDashboardService.getReplenishmentItemDetail(sku);
    }

    public OrderSummaryResponse getOrderSummary(Long storeId) {
        return orderDashboardService.getOrderSummary(storeId);
    }

    public OrderListResponse getOrderItems(Long storeId) {
        return orderDashboardService.getOrderItems(storeId);
    }

    public OrderDetailResponse getOrderItemDetail(String sku) {
        return orderDashboardService.getOrderItemDetail(sku);
    }
}