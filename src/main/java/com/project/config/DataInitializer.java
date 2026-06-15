package com.project.config;

import com.project.entity.Role;
import com.project.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_MERCHANT");

    }

    private void createRoleIfNotFound(String roleName) {
        if(!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
