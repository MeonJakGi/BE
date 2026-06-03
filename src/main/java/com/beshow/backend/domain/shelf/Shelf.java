package com.beshow.backend.domain.shelf;

import com.beshow.backend.domain.store.Store;
import com.beshow.backend.global.common.BaseTimeEntity;
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
@Table(name = "shelf")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shelf extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shelf_id")
    private Long shelfId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "shelf_name", nullable = false, length = 100)
    private String shelfName;

    @Column(name = "front_edge_points", columnDefinition = "json")
    private String frontEdgePoints;
}