package com.beshow.backend.domain.inventory;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	List<Inventory> findByStoreStoreId(Long storeId);
    Optional<Inventory> findByProductSkuCode(String skuCode);

    Optional<Inventory> findByStoreStoreIdAndProductProductId(
			Long storeId,
			Long productId
	);

    @Query("""
            select i
            from Inventory i
            join fetch i.store store
            join fetch i.product product
            where product.productId in :productIds
            	and (:storeId is null or store.storeId = :storeId)
            """)
    List<Inventory> findByProductIdsAndOptionalStoreId(
            @Param("productIds") List<Long> productIds,
            @Param("storeId") Long storeId
    );

    @Query("""
            select i
            from Inventory i
            join fetch i.store store
            join fetch i.product product
            where i.totalQuantity <= i.reorderPoint
            	and (:storeId is null or store.storeId = :storeId)
            order by
            case when i.orderCompleted = false then 0 else 1 end,
            i.updatedAt desc
             """)
    List<Inventory> findOrderNeededItems(@Param("storeId") Long storeId);

    @Query("""
            select i
            from Inventory i
            join fetch i.product product
            where product.skuCode = :sku
            """)
    Optional<Inventory> findBySku(@Param("sku") String sku);

    @Query("""
            select i
            from Inventory i
            join fetch i.store store
            join fetch i.product product
            where i.totalQuantity <= i.reorderPoint
            	and i.orderCompleted = false
            	and (:storeId is null or store.storeId = :storeId)
            """)
    List<Inventory> findUncompletedOrderNeededItems(@Param("storeId") Long storeId);
}