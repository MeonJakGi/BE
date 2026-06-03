package com.beshow.backend.dashboard.controller;

import com.beshow.backend.dashboard.dto.StoreResponse;
import com.beshow.backend.dashboard.service.StoreService;
import com.beshow.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/beshow/dashboard")
public class DashboardController {

    private final StoreService storeService;

    @GetMapping("/stores")
    public ApiResponse<List<StoreResponse>> getStores() {
        return ApiResponse.success(storeService.getStores(), "점포 목록 조회 성공");
    }
}