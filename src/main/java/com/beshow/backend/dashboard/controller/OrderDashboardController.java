package com.beshow.backend.dashboard.controller;

import com.beshow.backend.dashboard.dto.CompleteOrderResponse;
import com.beshow.backend.dashboard.dto.OrderDetailResponse;
import com.beshow.backend.dashboard.dto.OrderListResponse;
import com.beshow.backend.dashboard.dto.OrderSummaryResponse;
import com.beshow.backend.dashboard.dto.UpdateOrderCompletedRequest;
import com.beshow.backend.dashboard.service.DashboardService;
import com.beshow.backend.dashboard.service.OrderCheckService;
import com.beshow.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beshow/dashboard/order")
public class OrderDashboardController {

    private final DashboardService dashboardService;
    private final OrderCheckService orderCheckService;

    @GetMapping("/summary")
    public ApiResponse<OrderSummaryResponse> getSummary(
            @RequestParam(required = false) Long storeId
    ) {
        OrderSummaryResponse response = dashboardService.getOrderSummary(storeId);
        return ApiResponse.success(response, "발주 필요 요약 조회 성공");
    }

    @GetMapping("/items")
    public ApiResponse<OrderListResponse> getItems(
            @RequestParam(required = false) Long storeId
    ) {
        OrderListResponse response = dashboardService.getOrderItems(storeId);
        return ApiResponse.success(response, "발주 필요 리스트 조회 성공");
    }

    @GetMapping("/item/{sku}")
    public ApiResponse<OrderDetailResponse> getDetail(@PathVariable String sku) {
        OrderDetailResponse response = dashboardService.getOrderItemDetail(sku);
        return ApiResponse.success(response, "발주 SKU 상세 조회 성공");
    }

    @PostMapping("/item/{sku}/complete")
    public ApiResponse<CompleteOrderResponse> updateOrderCompleted(
            @PathVariable String sku,
            @RequestBody UpdateOrderCompletedRequest request
    ) {
        CompleteOrderResponse response = orderCheckService.updateOrderCompleted(
                sku,
                request.orderCompleted()
        );

        return ApiResponse.success(response, "발주 완료 체크 상태 변경 성공");
    }
}