package com.beshow.backend.dashboard.service;

import com.beshow.backend.dashboard.dto.CompleteOrderResponse;
import com.beshow.backend.domain.inventory.Inventory;
import com.beshow.backend.domain.inventory.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCheckService {

    private final InventoryRepository inventoryRepository;

    public CompleteOrderResponse updateOrderCompleted(String sku, boolean orderCompleted) {
        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("재고 정보를 찾을 수 없습니다. sku=" + sku));

        inventory.updateOrderCompleted(orderCompleted);

        return new CompleteOrderResponse(
                sku,
                inventory.isOrderCompleted(),
                inventory.getOrderCompletedAt()
        );
    }
}