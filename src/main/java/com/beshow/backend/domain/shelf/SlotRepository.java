package com.beshow.backend.domain.shelf;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByShelfShelfIdOrderByRowNoAscColNoAsc(Long shelfId);
}