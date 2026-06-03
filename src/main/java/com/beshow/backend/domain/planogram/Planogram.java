package com.beshow.backend.domain.planogram;

import com.beshow.backend.global.common.BaseTimeEntity;
import com.beshow.backend.domain.product.Product;
import com.beshow.backend.domain.shelf.Slot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "planogram")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planogram extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planogram_id")
    private Long planogramId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "expected_quantity", nullable = false)
    private Integer expectedQuantity;

    @Column(name = "min_front_quantity", nullable = false)
    private Integer minFrontQuantity;

    @Column(name = "min_display_quantity", nullable = false)
    private Integer minDisplayQuantity;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
