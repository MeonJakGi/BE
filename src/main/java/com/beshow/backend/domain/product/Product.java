package com.beshow.backend.domain.product;

import com.beshow.backend.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "class_id")
    private Integer classId;

    @Column(name = "sku_code", nullable = false, length = 50, unique = true)
    private String skuCode;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "barcode", length = 100, unique = true)
    private String barcode;

    @Column(name = "product_image_url", length = 500)
    private String productImageUrl;

    @Column(name = "unit", length = 30)
    private String unit;
}
