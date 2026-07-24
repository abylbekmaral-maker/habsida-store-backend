package com.project.service;

import com.project.repository.UserStoreAccessRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreAccessServiceTest {

    @Mock
    private UserStoreAccessRepository userStoreAccessRepository;

    @InjectMocks
    private StoreAccessService storeAccessService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAllowMerchantAccessToOwnStore() {

        setAuthentication("merchant", "ROLE_MERCHANT");

        when(userStoreAccessRepository
                .existsByUserUsernameAndStoreSlugAndRoleName(
                        "merchant",
                        "my-store",
                        "ROLE_MERCHANT"
                ))
                .thenReturn(true);

        boolean result = storeAccessService.hasStoreAccess(
                "my-store",
                "ROLE_MERCHANT"
        );

        assertTrue(result);
    }

    @Test
    void shouldDenyMerchantAccessToAnotherStore() {

        setAuthentication("merchant", "ROLE_MERCHANT");

        when(userStoreAccessRepository
                .existsByUserUsernameAndStoreSlugAndRoleName(
                        "merchant",
                        "another-store",
                        "ROLE_MERCHANT"
                ))
                .thenReturn(false);

        boolean result = storeAccessService.hasStoreAccess(
                "another-store",
                "ROLE_MERCHANT"
        );

        assertFalse(result);
    }

    @Test
    void shouldAllowAdminAccessToAnyStore() {

        setAuthentication("admin", "ROLE_ADMIN");

        boolean result = storeAccessService.hasStoreAccess(
                "any-store",
                "ROLE_MERCHANT"
        );

        assertTrue(result);

        verifyNoInteractions(userStoreAccessRepository);
    }

    private void setAuthentication(String username, String role) {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}