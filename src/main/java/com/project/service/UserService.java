package com.project.service;

import com.project.entity.Role;
import com.project.entity.Store;
import com.project.entity.User;
import com.project.entity.UserStoreAccess;
import com.project.repository.RoleRepository;
import com.project.repository.StoreRepository;
import com.project.repository.UserRepository;
import com.project.repository.UserStoreAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;
    private final UserStoreAccessRepository userStoreAccessRepository;
    private final PasswordEncoder passwordEncoder;

    public User createMerchant(String username, String email, String password) {
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already taken");
        }
        if(userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already taken");
        }
        
        Role merchantRole = roleRepository.findByName("ROLE_MERCHANT")
                .orElseThrow(() -> new RuntimeException("ROLE_MERCHANT role not found"));

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(merchantRole));

        return userRepository.save(user);
    }

    public UserStoreAccess assignMerchantToStore(UUID userId, UUID storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Role merchantRole = roleRepository.findByName("ROLE_MERCHANT")
                .orElseThrow(() -> new RuntimeException("ROLE_MERCHANT role not found"));

        UserStoreAccess access = new UserStoreAccess();
        access.setUser(user);
        access.setStore(store);
        access.setRole(merchantRole);

        return userStoreAccessRepository.save(access);
    }
}
