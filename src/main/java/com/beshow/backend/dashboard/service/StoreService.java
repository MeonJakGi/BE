package com.beshow.backend.dashboard.service;

import com.beshow.backend.dashboard.dto.StoreResponse;
import com.beshow.backend.domain.store.Store;
import com.beshow.backend.domain.store.StoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreRepository storeRepository;

    public List<StoreResponse> getStores() {
        return storeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StoreResponse toResponse(Store store) {
        return new StoreResponse(
                store.getStoreId(),
                store.getStoreName()
        );
    }
}