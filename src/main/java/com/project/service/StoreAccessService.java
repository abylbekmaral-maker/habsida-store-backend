package com.project.service;

import com.project.entity.Store;
import com.project.entity.User;
import com.project.repository.UserStoreAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreAccessService {

    private final UserStoreAccessRepository userStoreAccessRepository;

    public boolean hasAccess(User user, Store store) {
        return userStoreAccessRepository.existsByUserAndStore(user, store);
    }
}