package com.beshow.backend.domain.stock;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findBySlotShelfShelfIdAndCurrentTrue(Long shelfId);

    @Query("""
            select s
            from Stock s
            join fetch s.product p
            join fetch s.slot slot
            join fetch slot.shelf shelf
            join fetch shelf.store store
            join fetch s.shelfImage shelfImage
            where s.current = true
              and (
                    s.status in :statuses
                    or s.misplaced = true
                  )
              and (:storeId is null or store.storeId = :storeId)
              and (:shelfId is null or shelf.shelfId = :shelfId)
            """)
    List<Stock> findCurrentReplenishmentStocks(
            @Param("storeId") Long storeId,
            @Param("shelfId") Long shelfId,
            @Param("statuses") List<StockStatus> statuses
    );

    @Query("""
            select s
            from Stock s
            join fetch s.product product
            join fetch s.slot slot
            where s.current = true
              and s.shelfImage.shelfImageId = :shelfImageId
            """)
    List<Stock> findCurrentByShelfImageId(@Param("shelfImageId") Long shelfImageId);

    @Query("""
            select s
            from Stock s
            join fetch s.product product
            join fetch s.slot slot
            join fetch slot.shelf shelf
            join fetch shelf.store store
            join fetch s.shelfImage shelfImage
            where s.current = true
              and product.skuCode = :sku
            order by s.changedAt desc
            """)
    List<Stock> findCurrentStocksBySku(@Param("sku") String sku);
}