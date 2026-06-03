package com.beshow.backend.domain.shelf;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {

    List<Shelf> findByStoreStoreId(Long storeId);
}