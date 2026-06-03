package com.beshow.backend.domain.store;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findFirstByOrderByStoreIdAsc();
}