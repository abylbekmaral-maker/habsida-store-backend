package com.project.config;

import com.project.repository.*;
import com.project.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ModifierGroupRepository modifierGroupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (storeRepository.count() > 0) {
            log.info("Database is already seeded. Skipping...");
            return;
        }

        log.info("Starting database seeding for DEV environment...");

        User devUser = new User();
        devUser.setUsername("dev");
        devUser.setEmail("devUser@gmail.com");
        devUser.setPassword("devUser");

        devUser = userRepository.save(devUser);


        Store store = new Store();
        store.setName("Fruit Shop");
        store.setSlug("fruit-shop");
        store.setOwner(devUser);
        storeRepository.save(store);

        Category applesCategory = new Category();
        applesCategory.setName("Apples");
        applesCategory.setSlug("apples");
        applesCategory.setStore(store);
        categoryRepository.save(applesCategory);

        Category citrusCategory = new Category();
        citrusCategory.setName("Citrus");
        citrusCategory.setSlug("citrus");
        citrusCategory.setStore(store);
        categoryRepository.save(citrusCategory);

        Product antonovka = new Product();
        antonovka.setName("Antonovka Apple");
        antonovka.setDescription("A traditional domestic variety with a strong aroma and pleasant acidity, ideal for pickling and home baking.");
        antonovka.setPrice(new BigDecimal("5.50"));
        antonovka.setStock(500);
        antonovka.setLowStockThreshold(20);
        antonovka.setMinQuantity(1);
        antonovka.setPauseOrdering(false);
        antonovka.setStore(store);
        antonovka.setCategory(applesCategory);
        productRepository.save(antonovka);

        Product grannySmith = new Product();
        grannySmith.setName("Granny Smith Apple");
        grannySmith.setDescription("Characteristic sweet and sour taste, firm consistency, relatively little sugar, virtually no aroma");
        grannySmith.setPrice(new BigDecimal("6.00"));
        grannySmith.setStock(300);
        grannySmith.setLowStockThreshold(30);
        grannySmith.setMinQuantity(1);
        grannySmith.setPauseOrdering(false);
        grannySmith.setStore(store);
        grannySmith.setCategory(applesCategory);
        productRepository.save(grannySmith);

        ModifierGroup packingGroup = new ModifierGroup();
        packingGroup.setStore(store);
        packingGroup.setName("Type packing");
        packingGroup.setRequired(true);
        packingGroup.setMinSelect(1);
        packingGroup.setMaxSelect(1);

        ModifierOption plasticBag = new ModifierOption();
        plasticBag.setName("Plastic Bag");
        plasticBag.setPrice(BigDecimal.ZERO);
        packingGroup.addOption(plasticBag);

        ModifierOption box = new ModifierOption();
        box.setName("Box");
        box.setPrice(new BigDecimal("0.50"));
        packingGroup.addOption(box);

        ModifierOption bag = new ModifierOption();
        bag.setName("Bag");
        bag.setPrice(new BigDecimal("1.00"));
        packingGroup.addOption(bag);

        ModifierGroup savedGroup = modifierGroupRepository.save(packingGroup);

        antonovka.addModifierGroup(savedGroup);
        grannySmith.addModifierGroup(savedGroup);

        productRepository.save(antonovka);
        productRepository.save(grannySmith);

        log.info("Fruit store test data loaded successfully!");
    }
}