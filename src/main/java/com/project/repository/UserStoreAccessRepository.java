package com.project.repository;

import com.project.entity.Store;
import com.project.entity.User;
import com.project.entity.UserStoreAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserStoreAccessRepository extends JpaRepository<UserStoreAccess, UUID> {

    boolean existsByUserAndStore(User user, Store store);

    List<UserStoreAccess> findByUser(User user);
}