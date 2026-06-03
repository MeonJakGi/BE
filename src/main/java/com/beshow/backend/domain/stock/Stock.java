package com.beshow.backend.domain.stock;

import com.beshow.backend.domain.product.Product;
import com.beshow.backend.domain.shelf.ShelfImage;
import com.beshow.backend.domain.shelf.Slot;
import com.beshow.backend.global.common.BaseTimeEntity;
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
@Table(name = "stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shelf_image_id", nullable = false)
    private ShelfImage shelfImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StockStatus status;

    @Column(name = "is_misplaced", nullable = false)
    private boolean misplaced;

    @Column(name = "front_quantity", nullable = false)
    private Integer frontQuantity;

    @Column(name = "back_quantity", nullable = false)
    private Integer backQuantity;

    @Column(name = "detected_quantity", nullable = false)
    private Integer detectedQuantity;

    @Column(name = "estimated_shelf_quantity")
    private Integer estimatedShelfQuantity;

    @Column(name = "confidence", precision = 5, scale = 2)
    private BigDecimal confidence;

    @Column(name = "status_reason", length = 100)
    private String statusReason;

    @Column(name = "issue_x")
    private Integer issueX;

    @Column(name = "issue_y")
    private Integer issueY;

    @Column(name = "issue_width")
    private Integer issueWidth;

    @Column(name = "issue_height")
    private Integer issueHeight;

    @Column(name = "bbox_source", length = 50)
    private String bboxSource;

    @Column(name = "is_current", nullable = false)
    private boolean current;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public static Stock create(
            ShelfImage shelfImage,
            Slot slot,
            Product product,
            StockStatus status,
            boolean misplaced,
            Integer frontQuantity,
            Integer backQuantity,
            Integer detectedQuantity,
            Integer estimatedShelfQuantity,
            BigDecimal confidence,
            String statusReason,
            Integer issueX,
            Integer issueY,
            Integer issueWidth,
            Integer issueHeight,
            String bboxSource
    ) {
        Stock stock = new Stock();
        stock.shelfImage = shelfImage;
        stock.slot = slot;
        stock.product = product;
        stock.status = status;
        stock.misplaced = misplaced;
        stock.frontQuantity = frontQuantity;
        stock.backQuantity = backQuantity;
        stock.detectedQuantity = detectedQuantity;
        stock.estimatedShelfQuantity = estimatedShelfQuantity;
        stock.confidence = confidence;
        stock.statusReason = statusReason;
        stock.issueX = issueX;
        stock.issueY = issueY;
        stock.issueWidth = issueWidth;
        stock.issueHeight = issueHeight;
        stock.bboxSource = bboxSource;
        stock.current = true;
        stock.changedAt = LocalDateTime.now();
        return stock;
    }

    public void expire() {
        this.current = false;
    }
}