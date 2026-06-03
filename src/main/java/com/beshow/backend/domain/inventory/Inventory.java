package com.beshow.backend.domain.inventory;

import com.beshow.backend.domain.product.Product;
import com.beshow.backend.domain.store.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "reorder_point", nullable = false)
    private Integer reorderPoint;

    @Column(name = "recommended_order_quantity", nullable = false)
    private Integer recommendedOrderQuantity;

    @Column(name = "lead_time_days")
    private Integer leadTimeDays;

    @Column(name = "is_order_completed", nullable = false)
    private boolean orderCompleted;

    @Column(name = "order_completed_at")
    private LocalDateTime orderCompletedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public boolean isOrderCompleted() {
        return this.orderCompleted;
    }

    public boolean isOrderNeeded() {
        return this.totalQuantity <= this.reorderPoint;
    }

    public void completeOrder() {
        this.orderCompleted = true;
        this.orderCompletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateOrderCompleted(boolean orderCompleted) {
        this.orderCompleted = orderCompleted;
        this.orderCompletedAt = orderCompleted ? LocalDateTime.now() : null;
        this.updatedAt = LocalDateTime.now();
    }
}
