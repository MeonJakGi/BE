package com.beshow.backend.dashboard.service;

import com.beshow.backend.dashboard.dto.CategoryDistributionResponse;
import com.beshow.backend.dashboard.dto.OrderDetailResponse;
import com.beshow.backend.dashboard.dto.OrderInventoryResponse;
import com.beshow.backend.dashboard.dto.OrderItemResponse;
import com.beshow.backend.dashboard.dto.OrderListResponse;
import com.beshow.backend.dashboard.dto.OrderSummaryResponse;
import com.beshow.backend.dashboard.dto.TaskLocationResponse;
import com.beshow.backend.domain.inventory.Inventory;
import com.beshow.backend.domain.inventory.InventoryRepository;
import com.beshow.backend.domain.planogram.Planogram;
import com.beshow.backend.domain.planogram.PlanogramRepository;
import com.beshow.backend.domain.product.Product;
import com.beshow.backend.global.service.S3UrlService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderDashboardService {

    private static final String ORDER_REQUIRED_STATUS = "ORDER_REQUIRED";
    private static final String ORDER_REQUIRED_LABEL = "발주 필요";
    private static final String DEFAULT_LABEL = "-";

    private final InventoryRepository inventoryRepository;
    private final PlanogramRepository planogramRepository;
    private final S3UrlService s3UrlService;

    public OrderSummaryResponse getOrderSummary(Long storeId) {
        List<Inventory> orderNeededItems = inventoryRepository.findUncompletedOrderNeededItems(storeId);

        int orderRequiredSkuCount = orderNeededItems.size();

        int soldOutSkuCount = (int) orderNeededItems.stream()
                .filter(inventory -> valueOrZero(inventory.getTotalQuantity()) == 0)
                .count();

        List<CategoryDistributionResponse> categoryDistribution = orderNeededItems.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        inventory -> inventory.getProduct().getCategory() == null
                                ? "미분류"
                                : inventory.getProduct().getCategory(),
                        java.util.stream.Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> new CategoryDistributionResponse(
                        entry.getKey(),
                        entry.getValue().intValue()
                ))
                .toList();

        LocalDateTime lastUpdatedAt = orderNeededItems.stream()
                .map(Inventory::getUpdatedAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new OrderSummaryResponse(
                orderRequiredSkuCount,
                soldOutSkuCount,
                lastUpdatedAt,
                categoryDistribution
        );
    }

    public OrderListResponse getOrderItems(Long storeId) {
        List<Inventory> orderNeededItems = inventoryRepository.findOrderNeededItems(storeId);

        List<OrderItemResponse> items = orderNeededItems.stream()
                .map(this::toOrderItemResponse)
                .toList();

        return new OrderListResponse(items);
    }

    public OrderDetailResponse getOrderItemDetail(String sku) {
        Inventory inventory = inventoryRepository.findByProductSkuCode(sku)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 SKU의 재고 정보를 찾을 수 없습니다. sku=" + sku
                ));

        Product product = inventory.getProduct();

        int totalQuantity = valueOrZero(inventory.getTotalQuantity());
        int reorderPoint = valueOrZero(inventory.getReorderPoint());

        return new OrderDetailResponse(
                product.getSkuCode(),
                product.getProductName(),
                s3UrlService.createProductImageUrl(product.getProductImageUrl()),
                ORDER_REQUIRED_STATUS,
                ORDER_REQUIRED_LABEL,
                createOrderInventoryResponse(inventory),
                createOrderLocationResponse(product.getProductId()),
                createOrderReasonSummary(totalQuantity, reorderPoint),
                inventory.getUpdatedAt()
        );
    }

    private OrderItemResponse toOrderItemResponse(Inventory inventory) {
        Product product = inventory.getProduct();

        int totalQuantity = valueOrZero(inventory.getTotalQuantity());
        int reorderPoint = valueOrZero(inventory.getReorderPoint());

        TaskLocationResponse location = createOrderLocationResponse(product.getProductId());

        return new OrderItemResponse(
                product.getSkuCode(),
                product.getProductName(),
                ORDER_REQUIRED_STATUS,
                inventory.getUpdatedAt(),
                totalQuantity,
                valueOrDefault(location.shelfName()),
                valueOrDefault(location.slotCode()),
                inventory.isOrderCompleted(),
                reorderPoint,
                valueOrZero(inventory.getRecommendedOrderQuantity()),
                inventory.getLeadTimeDays(),
                createOrderReasonSummary(totalQuantity, reorderPoint),
                s3UrlService.createProductImageUrl(product.getProductImageUrl()),
                product.getCategory() == null ? "미분류" : product.getCategory()
        );
    }

    private OrderInventoryResponse createOrderInventoryResponse(Inventory inventory) {
        return new OrderInventoryResponse(
                valueOrZero(inventory.getTotalQuantity()),
                valueOrZero(inventory.getReorderPoint()),
                valueOrZero(inventory.getRecommendedOrderQuantity()),
                inventory.getLeadTimeDays(),
                inventory.isOrderCompleted()
        );
    }

    private TaskLocationResponse createOrderLocationResponse(Long productId) {
        List<Planogram> planograms = planogramRepository.findActiveByProductId(productId);

        if (planograms.isEmpty()) {
            return new TaskLocationResponse(null, null, null, null, DEFAULT_LABEL);
        }

        Planogram planogram = planograms.get(0);

        if (planogram.getSlot() == null) {
            return new TaskLocationResponse(null, null, null, null, DEFAULT_LABEL);
        }

        Long slotId = planogram.getSlot().getSlotId();
        String slotCode = planogram.getSlot().getSlotCode();

        if (planogram.getSlot().getShelf() == null) {
            return new TaskLocationResponse(
                    null,
                    null,
                    slotId,
                    slotCode,
                    createLocationLabel(null, slotCode)
            );
        }

        Long shelfId = planogram.getSlot().getShelf().getShelfId();
        String shelfName = planogram.getSlot().getShelf().getShelfName();

        return new TaskLocationResponse(
                shelfId,
                shelfName,
                slotId,
                slotCode,
                createLocationLabel(shelfName, slotCode)
        );
    }

    private String createLocationLabel(String shelfName, String slotCode) {
        boolean hasShelfName = shelfName != null && !shelfName.isBlank();
        boolean hasSlotCode = slotCode != null && !slotCode.isBlank();

        if (!hasShelfName && !hasSlotCode) {
            return DEFAULT_LABEL;
        }

        if (!hasShelfName) {
            return slotCode;
        }

        if (!hasSlotCode) {
            return shelfName;
        }

        return shelfName + " " + slotCode;
    }

    private String createOrderReasonSummary(int totalQuantity, int reorderPoint) {
        if (totalQuantity == 0) {
            return "창고 재고가 0개로 전량 소진되어 발주가 필요합니다.";
        }

        if (totalQuantity <= reorderPoint) {
            return "창고 재고가 ROP 이하입니다.";
        }

        return "발주 기준 확인이 필요합니다.";
    }

    private String valueOrDefault(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_LABEL;
        }

        return value;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }
}
