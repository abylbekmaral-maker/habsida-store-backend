package com.project.service;

import com.project.dto.CreateStoreRequest;
import com.project.entity.Store;
import com.project.entity.User;
import com.project.repository.StoreRepository;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreAccessService storeAccessService;
    private final UserRepository userRepository;

    public Store createStore(CreateStoreRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Store store = new Store();
        store.setName(request.getName());
        store.setSlug(request.getSlug());
        store.setOwner(owner);

        return storeRepository.save(store);
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store getStoreForMerchant(UUID storeId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        if (!storeAccessService.hasAccess(user, store)) {
            throw new RuntimeException("Access denied");
        }

        return store;
    }
}