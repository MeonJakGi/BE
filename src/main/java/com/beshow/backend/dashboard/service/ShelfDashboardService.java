package com.beshow.backend.dashboard.service;

import com.beshow.backend.dashboard.dto.BboxResponse;
import com.beshow.backend.dashboard.dto.DetectionOverlayResponse;
import com.beshow.backend.dashboard.dto.LatestDetectionResponse;
import com.beshow.backend.domain.detection.DetectionResult;
import com.beshow.backend.domain.detection.DetectionResultRepository;
import com.beshow.backend.domain.product.Product;
import com.beshow.backend.domain.shelf.Shelf;
import com.beshow.backend.domain.shelf.ShelfImage;
import com.beshow.backend.domain.shelf.ShelfImageRepository;
import com.beshow.backend.domain.shelf.Slot;
import com.beshow.backend.domain.stock.Stock;
import com.beshow.backend.domain.stock.StockRepository;
import com.beshow.backend.domain.stock.StockStatus;
import com.beshow.backend.global.exception.BusinessException;
import com.beshow.backend.global.exception.ErrorCode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShelfDashboardService {

    private final ShelfImageRepository shelfImageRepository;
    private final DetectionResultRepository detectionResultRepository;
    private final StockRepository stockRepository;
    
    public LatestDetectionResponse getLatestDetection(
            Long shelfId,
            StockStatus status,
            Boolean misplaced
    ) {
        ShelfImage shelfImage = shelfImageRepository.findTopByCamera_Shelf_ShelfIdOrderByCapturedAtDesc(shelfId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        List<DetectionResult> detectionResults = detectionResultRepository.findByShelfImageIdWithProductAndSlot(
                shelfImage.getShelfImageId()
        );

        Map<String, Stock> stockMap = stockRepository.findCurrentByShelfImageId(shelfImage.getShelfImageId())
                .stream()
                .collect(Collectors.toMap(
                        stock -> stockKey(
                                stock.getProduct().getProductId(),
                                stock.getSlot().getSlotId()
                        ),
                        Function.identity(),
                        (left, right) -> left
                ));

        List<DetectionOverlayResponse> detections = detectionResults.stream()
                .map(detectionResult -> toOverlayResponse(detectionResult, stockMap))
                .filter(overlay -> overlay != null
                        && (status == null || overlay.status() == status))
                .toList();

        Shelf shelf = shelfImage.getCamera().getShelf();

        return new LatestDetectionResponse(
                shelf.getShelfId(),
                shelfImage.getShelfImageId(),
                shelfImage.getImageS3Key(),
                createImageUrl(shelfImage.getImageS3Key()),
                shelfImage.getImageWidth(),
                shelfImage.getImageHeight(),
                shelfImage.getCapturedAt(),
                shelf.getFrontEdgePoints(),
                detections
        );
    }

    private DetectionOverlayResponse toOverlayResponse(
            DetectionResult detectionResult,
            Map<String, Stock> stockMap
    ) {
        Product product = detectionResult.getProduct();
        Slot slot = detectionResult.getSlot();

        if (product == null || slot == null) {
            return null;
        }

        Stock stock = stockMap.get(stockKey(product.getProductId(), slot.getSlotId()));
        if (stock == null) {
            return null;
        }

        return new DetectionOverlayResponse(
                detectionResult.getDetectionResultId(),
                product.getSkuCode(),
                product.getProductName(),
                slot.getSlotCode(),
                new BboxResponse(
                        valueOrZero(detectionResult.getX()),
                        valueOrZero(detectionResult.getY()),
                        valueOrZero(detectionResult.getWidth()),
                        valueOrZero(detectionResult.getHeight())
                ),
                detectionResult.getDepthPosition(),
                detectionResult.getConfidence(),
                stock.getStatus(),
                statusLabel(stock)
        );
    }

    private String stockKey(Long productId, Long slotId) {
        return productId + ":" + slotId;
    }

    private int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    private static final String S3_BUCKET = "sesac-cctv";
    private static final String S3_REGION = "ap-northeast-2";

    private String createImageUrl(String imageS3Key) {
        if (imageS3Key == null || imageS3Key.isBlank()) {
            return null;
        }

        String encodedKey = Arrays.stream(imageS3Key.split("/"))
                .map(segment -> URLEncoder.encode(segment, StandardCharsets.UTF_8))
                .collect(Collectors.joining("/"));

        return "https://"
                + S3_BUCKET
                + ".s3."
                + S3_REGION
                + ".amazonaws.com/"
                + encodedKey;
    }

    private String statusLabel(Stock stock) {
        if (stock.isMisplaced()) {
            return "확인 필요";
        }

        return switch (stock.getStatus()) {
            case NEED_CHECK -> "확인 필요";
            case ENOUGH -> "충분";
            case NEED_REFILL -> "보충 필요";
            case ORDER_NEEDED -> "발주 필요";
        };
    }
}
