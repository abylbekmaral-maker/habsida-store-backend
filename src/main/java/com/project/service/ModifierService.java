package com.project.service;

import com.project.dto.ModifierGroupDto;
import com.project.dto.ModifierGroupResponseDto;
import com.project.dto.ModifierOptionDto;
import com.project.dto.ModifierOptionResponseDto;
import com.project.entity.ModifierGroup;
import com.project.entity.ModifierOption;
import com.project.entity.Product;
import com.project.entity.Store;
import com.project.repository.ModifierGroupRepository;
import com.project.repository.ProductRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModifierService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final ModifierGroupRepository modifierGroupRepository;

    @Transactional
    public ModifierGroupResponseDto createModifierGroup(String storeSlug, ModifierGroupDto dto) {
        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        ModifierGroup group = new ModifierGroup();
        group.setStore(store);
        group.setName(dto.name());
        group.setRequired(dto.isRequired());
        group.setMinSelect(dto.minSelect());
        group.setMaxSelect(dto.maxSelect());

        if (dto.options() != null) {
            for (ModifierOptionDto optionDto : dto.options()) {
                ModifierOption option = new ModifierOption();
                option.setName(optionDto.name());
                option.setPrice(optionDto.price());
                group.addOption(option);
            }
        }
        return toResponseDto(modifierGroupRepository.save(group));
    }

    @Transactional
    public void deleteModifierGroup(String storeSlug, UUID groupId) {
        ModifierGroup group = modifierGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Modifier Group not found"));

        if (!group.getStore().getSlug().equals(storeSlug)) {
            throw new IllegalArgumentException("Group does not belong to this store");
        }

        modifierGroupRepository.delete(group);
    }

    @Transactional
    public void linkModifierGroupToProduct(String storeSlug, UUID productId, UUID groupId) {
        Product product = getValidProduct(storeSlug, productId);
        ModifierGroup group = getValidGroup(storeSlug, groupId);

        if (!product.getModifierGroups().contains(group)) {
            product.addModifierGroup(group);
            productRepository.save(product);
        }
    }

    @Transactional
    public void unlinkModifierGroupFromProduct(String storeSlug, UUID productId, UUID groupId) {
        Product product = getValidProduct(storeSlug, productId);
        ModifierGroup group = getValidGroup(storeSlug, groupId);

        if (product.getModifierGroups().contains(group)) {
            product.removeModifierGroup(group);
            productRepository.save(product);
        }
    }

    private Product getValidProduct(String storeSlug, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (!product.getStore().getSlug().equals(storeSlug)) {
            throw new IllegalArgumentException("Product does not belong to this store");
        }
        return product;
    }

    private ModifierGroup getValidGroup(String storeSlug, UUID groupId) {
        ModifierGroup group = modifierGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Modifier Group not found"));
        if (!group.getStore().getSlug().equals(storeSlug)) {
            throw new IllegalArgumentException("Group does not belong to this store");
        }
        return group;
    }

    @Transactional(readOnly = true)
    public List<ModifierGroupResponseDto> getModifierGroupsByStore(String storeSlug) {
        return modifierGroupRepository.findAllByStoreSlug(storeSlug).stream()
                .map(this::toResponseDto)
                .toList();
    }
    @Transactional(readOnly = true)
    public ModifierGroupResponseDto getModifierGroupById(String storeSlug, UUID groupId) {
        return toResponseDto(getValidGroup(storeSlug, groupId));
    }

    @Transactional
    public ModifierGroupResponseDto updateModifierGroup(String storeSlug, UUID groupId, ModifierGroupDto dto) {
        ModifierGroup group = getValidGroup(storeSlug, groupId);

        group.setName(dto.name());
        group.setRequired(dto.isRequired());
        group.setMinSelect(dto.minSelect());
        group.setMaxSelect(dto.maxSelect());

        group.getOptions().clear();

        if (dto.options() != null) {
            for (ModifierOptionDto optionDto : dto.options()) {
                ModifierOption option = new ModifierOption();
                option.setName(optionDto.name());
                option.setPrice(optionDto.price());
                group.addOption(option);
            }
        }
        return toResponseDto(modifierGroupRepository.save(group));
    }

    public ModifierGroupResponseDto toResponseDto(ModifierGroup group) {
        List<ModifierOptionResponseDto> optionDtos = group.getOptions().stream()
                .map(opt -> new ModifierOptionResponseDto(opt.getId(), opt.getName(), opt.getPrice()))
                .toList();
        return new ModifierGroupResponseDto(
                group.getId(),
                group.getName(),
                group.isRequired(),
                group.getMinSelect(),
                group.getMaxSelect(),
                optionDtos
        );
    }
}
