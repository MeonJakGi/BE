package com.beshow.backend.dashboard.service;

import com.beshow.backend.dashboard.dto.DashboardSummaryCountResponse;
import com.beshow.backend.dashboard.dto.DashboardSummaryResponse;
import com.beshow.backend.dashboard.dto.ReplenishmentDetailResponse;
import com.beshow.backend.dashboard.dto.ReplenishmentItemResponse;
import com.beshow.backend.dashboard.dto.ReplenishmentListResponse;
import com.beshow.backend.dashboard.dto.TaskLocationResponse;
import com.beshow.backend.domain.inventory.Inventory;
import com.beshow.backend.domain.inventory.InventoryRepository;
import com.beshow.backend.domain.planogram.Planogram;
import com.beshow.backend.domain.planogram.PlanogramRepository;
import com.beshow.backend.domain.product.Product;
import com.beshow.backend.domain.shelf.Shelf;
import com.beshow.backend.domain.shelf.ShelfImage;
import com.beshow.backend.domain.shelf.Slot;
import com.beshow.backend.domain.stock.Stock;
import com.beshow.backend.domain.stock.StockRepository;
import com.beshow.backend.domain.stock.StockStatus;
import com.beshow.backend.domain.store.Store;
import com.beshow.backend.domain.store.StoreRepository;
import com.beshow.backend.global.exception.BusinessException;
import com.beshow.backend.global.exception.ErrorCode;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplenishmentDashboardService {

    private static final List<StockStatus> SCR1_SUMMARY_STATUSES = List.of(
            StockStatus.ENOUGH,
            StockStatus.NEED_REFILL,
            StockStatus.NEED_CHECK
    );

    private static final List<StockStatus> SCR1_ITEM_STATUSES = List.of(
            StockStatus.NEED_REFILL,
            StockStatus.NEED_CHECK
    );

    private final StockRepository stockRepository;
    private final InventoryRepository inventoryRepository;
    private final PlanogramRepository planogramRepository;
    private final StoreRepository storeRepository;


    public DashboardSummaryResponse getReplenishmentSummary(Long storeId, Long shelfId) {
            List<Stock> stocks = stockRepository.findCurrentReplenishmentStocks(
            storeId,
            shelfId,
            SCR1_SUMMARY_STATUSES
    );
        Store store = resolveStore(storeId);

        return new DashboardSummaryResponse(
                store == null ? storeId : store.getStoreId(),
                store == null ? null : store.getStoreName(),
                lastUpdatedAt(stocks),
                new DashboardSummaryCountResponse(
                        countByStatus(stocks, StockStatus.ENOUGH),
                        countByStatus(stocks, StockStatus.NEED_REFILL),
                        countNeedCheck(stocks)
                )
        );
    }

    public ReplenishmentListResponse getReplenishmentItems(
            Long storeId,
            Long shelfId,
            StockStatus status,
            Boolean misplaced
    ) {
        List<StockStatus> statuses = resolveStatuses(status);

        List<Stock> stocks = stockRepository.findCurrentReplenishmentStocks(
                storeId,
                shelfId,
                statuses
        );

        List<Stock> filteredStocks = filterByMisplaced(stocks, misplaced);
        Map<String, Inventory> inventoryMap = findInventoryMap(storeId, filteredStocks);
        Map<String, Planogram> planogramMap = findPlanogramMap(filteredStocks);

        List<ReplenishmentItemResponse> items = filteredStocks.stream()
                .sorted(Comparator
                        .comparingInt(this::priority)
                        .thenComparing(
                                stock -> refillShortageQuantity(stock, planogramMap),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(stock -> stock.getProduct().getSkuCode()))
                .map(stock -> toResponse(stock, inventoryMap))
                .toList();

        return new ReplenishmentListResponse(items);
    }

    public ReplenishmentDetailResponse getReplenishmentItemDetail(String sku) {
        Stock stock = stockRepository.findCurrentStocksBySku(sku)
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Product product = stock.getProduct();
        Slot slot = stock.getSlot();
        Shelf shelf = slot.getShelf();
        ShelfImage shelfImage = stock.getShelfImage();

        Inventory inventory = inventoryRepository
                .findByStoreStoreIdAndProductProductId(
                        shelf.getStore().getStoreId(),
                        product.getProductId()
                )
                .orElse(null);

        String shelfName = createShelfName(shelf);
        String locationLabel = shelfName + " " + slot.getSlotCode();

    return new ReplenishmentDetailResponse(
            product.getSkuCode(),
            product.getProductName(),
            product.getProductImageUrl(),
            stock.getStatus(),
            stock.isMisplaced(),
            statusLabel(stock),
            inventory == null ? 0 : valueOrZero(inventory.getTotalQuantity()),
            valueOrZero(stock.getFrontQuantity()),
            valueOrZero(stock.getBackQuantity()),
            valueOrZero(stock.getDetectedQuantity()),
            stock.getEstimatedShelfQuantity(),
            stock.getConfidence(),
            new TaskLocationResponse(
                    shelf.getShelfId(),
                    shelfName,
                    slot.getSlotId(),
                    slot.getSlotCode(),
                    locationLabel
            ),
            detectedAt(stock, shelfImage),
            stock.getStatusReason()
        );
    }
    private List<StockStatus> resolveStatuses(StockStatus status) {
        if (status == null) {
            return SCR1_ITEM_STATUSES;
        }

        return List.of(status);
    }

    private List<Stock> filterByMisplaced(List<Stock> stocks, Boolean misplaced) {
        if (misplaced == null) {
            return stocks;
        }

        return stocks.stream()
                .filter(stock -> stock.isMisplaced() == misplaced)
                .toList();
    }

    private Map<String, Inventory> findInventoryMap(Long storeId, List<Stock> stocks) {
        List<Long> productIds = stocks.stream()
                .map(Stock::getProduct)
                .filter(Objects::nonNull)
                .map(Product::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (productIds.isEmpty()) {
            return Map.of();
        }

        return inventoryRepository.findByProductIdsAndOptionalStoreId(productIds, storeId)
                .stream()
                .collect(Collectors.toMap(
                        inventory -> inventoryKey(
                                inventory.getStore().getStoreId(),
                                inventory.getProduct().getProductId()
                        ),
                        Function.identity(),
                        (left, right) -> left
                ));
    }

    private Map<String, Planogram> findPlanogramMap(List<Stock> stocks) {
        List<Long> slotIds = stocks.stream()
                .map(Stock::getSlot)
                .filter(Objects::nonNull)
                .map(Slot::getSlotId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Long> productIds = stocks.stream()
                .map(Stock::getProduct)
                .filter(Objects::nonNull)
                .map(Product::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (slotIds.isEmpty() || productIds.isEmpty()) {
            return Map.of();
        }

        return planogramRepository.findActiveBySlotIdsAndProductIds(slotIds, productIds)
                .stream()
                .collect(Collectors.toMap(
                        planogram -> planogramKey(
                                planogram.getSlot().getSlotId(),
                                planogram.getProduct().getProductId()
                        ),
                        Function.identity(),
                        (left, right) -> left
                ));
    }

    private ReplenishmentItemResponse toResponse(
            Stock stock,
            Map<String, Inventory> inventoryMap
    ) {
        Product product = stock.getProduct();
        Slot slot = stock.getSlot();
        Shelf shelf = slot.getShelf();
        ShelfImage shelfImage = stock.getShelfImage();

        Inventory inventory = inventoryMap.get(inventoryKey(
                shelf.getStore().getStoreId(),
                product.getProductId()
        ));

        String shelfName = createShelfName(shelf);
        String locationLabel = shelfName + " " + slot.getSlotCode();

        return new ReplenishmentItemResponse(
                product.getSkuCode(),
                product.getProductName(),
                product.getProductImageUrl(),
                stock.getStatus(),
                stock.isMisplaced(),
                statusLabel(stock),
                inventory == null ? 0 : valueOrZero(inventory.getTotalQuantity()),
                valueOrZero(stock.getFrontQuantity()),
                valueOrZero(stock.getBackQuantity()),
                valueOrZero(stock.getDetectedQuantity()),
                stock.getEstimatedShelfQuantity(),
                stock.getConfidence(),
                shelf.getShelfId(),
                shelfName,
                slot.getSlotId(),
                slot.getSlotCode(),
                locationLabel,
                detectedAt(stock, shelfImage),
                priority(stock),
                stock.getStatusReason()
        );
    }

    private Store resolveStore(Long storeId) {
        if (storeId != null) {
            return storeRepository.findById(storeId).orElse(null);
        }

        return storeRepository.findFirstByOrderByStoreIdAsc().orElse(null);
    }

    private LocalDateTime lastUpdatedAt(List<Stock> stocks) {
        return stocks.stream()
                .map(Stock::getChangedAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    private int countByStatus(List<Stock> stocks, StockStatus status) {
        return (int) stocks.stream()
                .filter(stock -> stock.getStatus() == status)
                .count();
    }

    private int countNeedCheck(List<Stock> stocks) {
        return (int) stocks.stream()
                .filter(stock -> stock.getStatus() == StockStatus.NEED_CHECK || stock.isMisplaced())
                .count();
    }

    private int priority(Stock stock) {
        if (stock.isMisplaced()) {
            return 1;
        }

        return switch (stock.getStatus()) {
            case NEED_CHECK -> 1;
            case NEED_REFILL -> 2;
            case ORDER_NEEDED -> 3;
            case ENOUGH -> 4;
        };
    }

    private int refillShortageQuantity(Stock stock, Map<String, Planogram> planogramMap) {
        if (stock.getStatus() != StockStatus.NEED_REFILL) {
            return 0;
        }

        Planogram planogram = planogramMap.get(planogramKey(
                stock.getSlot().getSlotId(),
                stock.getProduct().getProductId()
        ));

        if (planogram == null) {
            return 0;
        }

        int expectedQuantity = valueOrZero(planogram.getExpectedQuantity());
        int detectedQuantity = valueOrZero(stock.getDetectedQuantity());

        return Math.max(expectedQuantity - detectedQuantity, 0);
    }

    private String inventoryKey(Long storeId, Long productId) {
        return storeId + ":" + productId;
    }

    private String planogramKey(Long slotId, Long productId) {
        return slotId + ":" + productId;
    }

    private String createShelfName(Shelf shelf) {
        if (shelf.getShelfName() != null && !shelf.getShelfName().isBlank()) {
            return shelf.getShelfName();
        }

        return "선반 " + shelf.getShelfId();
    }

    private LocalDateTime detectedAt(Stock stock, ShelfImage shelfImage) {
        if (shelfImage != null && shelfImage.getCapturedAt() != null) {
            return shelfImage.getCapturedAt();
        }

        return stock.getChangedAt();
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private String statusLabel(Stock stock) {
        return statusLabel(stock.getStatus(), stock.isMisplaced());
    }

    private String statusLabel(StockStatus status, boolean misplaced) {
        if (misplaced) {
            return "확인 필요";
        }

        return switch (status) {
            case NEED_CHECK -> "확인 필요";
            case NEED_REFILL -> "보충 필요";
            case ORDER_NEEDED -> "발주 필요";
            case ENOUGH -> "충분";
        };
    }
}