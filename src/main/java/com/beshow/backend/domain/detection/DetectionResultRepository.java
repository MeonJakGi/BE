package com.beshow.backend.domain.detection;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetectionResultRepository extends JpaRepository<DetectionResult, Long> {

    @Query("""
            select dr
            from DetectionResult dr
            left join fetch dr.product product
            left join fetch dr.slot slot
            where dr.shelfImage.shelfImageId = :shelfImageId
            """)
    List<DetectionResult> findByShelfImageIdWithProductAndSlot(@Param("shelfImageId") Long shelfImageId);
}
