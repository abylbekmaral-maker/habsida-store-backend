package com.project.repository;

import com.project.entity.Store;
import com.project.entity.User;
import com.project.entity.UserStoreAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserStoreAccessRepository extends JpaRepository<UserStoreAccess, UUID> {

    boolean existsByUserAndStore(User user, Store store);

    List<UserStoreAccess> findByUser(User user);

    boolean existsByUserUsernameAndStoreSlugAndRoleName(String username, String storeSlug, String roleName);
}