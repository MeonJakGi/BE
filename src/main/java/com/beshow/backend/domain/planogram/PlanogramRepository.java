package com.beshow.backend.domain.planogram;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanogramRepository extends JpaRepository<Planogram, Long> {

    @Query("""
            select p
            from Planogram p
            join fetch p.slot slot
            join fetch p.product product
            where slot.shelf.shelfId = :shelfId
              and p.active = true
            """)
    List<Planogram> findActiveByShelfId(@Param("shelfId") Long shelfId);

    @Query("""
            select p
            from Planogram p
            join fetch p.slot slot
            join fetch p.product product
            where slot.slotId = :slotId
              and p.active = true
            """)
    List<Planogram> findActiveBySlotId(@Param("slotId") Long slotId);

    @Query("""
            select p
            from Planogram p
            join fetch p.slot slot
            join fetch slot.shelf shelf
            join fetch p.product product
            where product.productId = :productId
              and p.active = true
            """)
    List<Planogram> findActiveByProductId(@Param("productId") Long productId);

    @Query("""
            select p
            from Planogram p
            join fetch p.slot s
            join fetch p.product pr
            where s.shelf.shelfId = :shelfId
              and p.active = true
            """)
    List<Planogram> findActivePlanogramsByShelfId(Long shelfId);

    @Query("""
          select p
          from Planogram p
          join fetch p.slot slot
          join fetch p.product product
          where slot.slotId in :slotIds
            and product.productId in :productIds
            and p.active = true
          """)
  List<Planogram> findActiveBySlotIdsAndProductIds(
          @Param("slotIds") List<Long> slotIds,
          @Param("productIds") List<Long> productIds
  );

}