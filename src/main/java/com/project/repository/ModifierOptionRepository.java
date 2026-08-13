package com.project.repository;

import com.project.entity.ModifierOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ModifierOptionRepository extends JpaRepository<ModifierOption, UUID> {
}