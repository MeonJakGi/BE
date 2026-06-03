package com.beshow.backend.domain.alarm;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("""
            select a
            from Alarm a
            join fetch a.stock stock
            join fetch stock.product product
            join fetch stock.slot slot
            join fetch slot.shelf shelf
            order by a.createdAt desc
            """)
    List<Alarm> findAllWithStockProductAndLocationOrderByCreatedAtDesc();

    long countByReadFalse();
}