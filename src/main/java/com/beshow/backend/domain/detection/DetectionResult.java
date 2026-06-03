package com.beshow.backend.domain.detection;

import com.beshow.backend.domain.product.Product;
import com.beshow.backend.domain.shelf.ShelfImage;
import com.beshow.backend.domain.shelf.Slot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detection_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetectionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detection_result_id")
    private Long detectionResultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_image_id", nullable = false)
    private ShelfImage shelfImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "x", nullable = false)
    private Integer x;

    @Column(name = "y", nullable = false)
    private Integer y;

    @Column(name = "width", nullable = false)
    private Integer width;

    @Column(name = "height", nullable = false)
    private Integer height;

    @Column(name = "confidence", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidence;

    @Enumerated(EnumType.STRING)
    @Column(name = "depth_position", length = 20)
    private DepthPosition depthPosition;

    @Column(name = "is_misplaced", nullable = false)
    private boolean misplaced;

    @Column(name = "is_low_confidence", nullable = false)
    private boolean lowConfidence;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    public static DetectionResult create(
            ShelfImage shelfImage,
            Slot slot,
            Product product,
            Integer x,
            Integer y,
            Integer width,
            Integer height,
            BigDecimal confidence,
            DepthPosition depthPosition,
            boolean misplaced,
            boolean lowConfidence,
            LocalDateTime detectedAt
    ) {
        DetectionResult detectionResult = new DetectionResult();
        detectionResult.shelfImage = shelfImage;
        detectionResult.slot = slot;
        detectionResult.product = product;
        detectionResult.x = x;
        detectionResult.y = y;
        detectionResult.width = width;
        detectionResult.height = height;
        detectionResult.confidence = confidence;
        detectionResult.depthPosition = depthPosition;
        detectionResult.misplaced = misplaced;
        detectionResult.lowConfidence = lowConfidence;
        detectionResult.detectedAt = detectedAt;
        return detectionResult;
    }
}
