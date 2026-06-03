package com.beshow.backend.domain.shelf;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShelfImageRepository extends JpaRepository<ShelfImage, Long> {

    Optional<ShelfImage> findTopByCamera_Shelf_ShelfIdOrderByCapturedAtDesc(Long shelfId);
}