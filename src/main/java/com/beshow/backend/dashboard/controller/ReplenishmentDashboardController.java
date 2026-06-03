package com.beshow.backend.dashboard.controller;

import com.beshow.backend.dashboard.dto.DashboardSummaryResponse;
import com.beshow.backend.dashboard.dto.LatestDetectionResponse;
import com.beshow.backend.dashboard.dto.ReplenishmentDetailResponse;
import com.beshow.backend.dashboard.dto.ReplenishmentListResponse;
import com.beshow.backend.dashboard.service.DashboardService;
import com.beshow.backend.domain.stock.StockStatus;
import com.beshow.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beshow/dashboard/replenishment")
public class ReplenishmentDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> getSummary(
            @RequestParam(name = "store_id", required = false) Long storeId,
            @RequestParam(name = "shelf_id", required = false) Long shelfId
    ) {
        DashboardSummaryResponse response =
                dashboardService.getReplenishmentSummary(storeId, shelfId);

        return ApiResponse.success(response, "보충 필요 요약 조회 성공");
    }

    @GetMapping("/items")
    public ApiResponse<ReplenishmentListResponse> getItems(
            @RequestParam(name = "store_id", required = false) Long storeId,
            @RequestParam(name = "shelf_id", required = false) Long shelfId,
            @RequestParam(required = false) StockStatus status,
            @RequestParam(required = false) Boolean misplaced
    ) {
        ReplenishmentListResponse response =
                dashboardService.getReplenishmentItems(storeId, shelfId, status, misplaced);

        return ApiResponse.success(response, "보충 필요 리스트 조회 성공");
    }

    @GetMapping("/item/{sku}")
    public ApiResponse<ReplenishmentDetailResponse> getDetail(
            @PathVariable String sku
    ) {
        ReplenishmentDetailResponse response =
                dashboardService.getReplenishmentItemDetail(sku);

        return ApiResponse.success(response, "보충 SKU 상세 조회 성공");
    }

    @GetMapping("/shelf-image")
    public ApiResponse<LatestDetectionResponse> getShelfImage(
            @RequestParam(name = "shelf_id") Long shelfId,
            @RequestParam(required = false) StockStatus status,
            @RequestParam(required = false) Boolean misplaced
    ) {
        LatestDetectionResponse response =
                dashboardService.getShelfImage(shelfId, status, misplaced);

        return ApiResponse.success(response, "선반 이미지 조회 성공");
    }
}