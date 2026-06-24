package com.project.service;

import com.project.entity.Store;
import com.project.entity.User;
import com.project.repository.UserStoreAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("storeSecurity")
@RequiredArgsConstructor
public class StoreAccessService {

    private final UserStoreAccessRepository userStoreAccessRepository;

    public boolean hasAccess(User user, Store store) {
        return userStoreAccessRepository.existsByUserAndStore(user, store);
    }

    @Transactional (readOnly = true)
    public boolean hasStoreAccess(String storeSlug, String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String username = authentication.getName();

        return userStoreAccessRepository.existsByUserUsernameAndStoreSlugAndRoleName(username, storeSlug, roleName);
    }
}