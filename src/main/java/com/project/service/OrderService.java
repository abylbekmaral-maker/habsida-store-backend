package com.project.service;

import com.project.dto.OrderRequestDto;
import com.project.dto.OrderResponseDto;
import com.project.entity.*;
import com.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.project.dto.OrderItemRequestDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;
import com.project.dto.OrderItemResponseDto;
import java.util.List;
import com.project.dto.OrderItemModifierRequestDto;
import com.project.dto.OrderItemModifierResponseDto;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import com.project.exception.ResourceNotFoundException;
import com.project.exception.ConflictException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ModifierOptionRepository modifierOptionRepository;
    private final StoreAccessService storeAccessService;
    private final OrderTotalCalculator orderTotalCalculator;
    private final StoreDeliveryAreaRepository storeDeliveryAreaRepository;
    private final StoreDeliverySettingsRepository storeDeliverySettingsRepository;
    private final StoreHourRepository storeHourRepository;
    private final StoreDeliveryRestrictionRepository storeDeliveryRestrictionRepository;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {

        if (request.type() == OrderType.DELIVERY && (request.address() == null || request.address().isBlank())) {
            throw new ConflictException("Delivery address is required for DELIVERY orders");
        }

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        Customer customer;
        if (request.customerId() != null) {
            customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        } else {
            customer = customerRepository.findByPhone(request.phone())
                    .orElseGet(() -> createNewCustomer(request));
        }

        StoreDeliveryArea deliveryArea = storeDeliveryAreaRepository.findById(request.deliveryAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery area not found"));

        StoreDeliverySettings deliverySettings = storeDeliverySettingsRepository
                .findByStore(store)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery settings not found"));

        ZoneId storeZone = ZoneId.systemDefault();
        
        LocalTime nowTime = LocalTime.now(storeZone);
        LocalDate nowDate = LocalDate.now(storeZone);

        StoreHour storeHour = storeHourRepository
                .findByStoreSlugAndDayOfWeek(
                        store.getSlug(),
                        com.project.entity.DayOfWeek.valueOf(nowDate.getDayOfWeek().name())
                )
                .orElseThrow(() -> new ConflictException("Store is closed today"));
        if (storeHour.isClosed()) {
            throw new ConflictException("Store is closed today");
        }

        if (storeHour.getOpenTime() != null
                && nowTime.isBefore(storeHour.getOpenTime())) {
            throw new ConflictException("Store is not open yet");
        }

        if (storeHour.getCloseTime() != null
                && nowTime.isAfter(storeHour.getCloseTime())) {
            throw new ConflictException("Store is already closed");
        }

        if (storeHour.getLastOrderCutoffTime() != null
                && nowTime.isAfter(storeHour.getLastOrderCutoffTime())) {
            throw new ConflictException("Last order cutoff time has passed");
        }

        boolean onBreak = storeHour.getBreaks().stream()
                .anyMatch(storeBreak ->
                        !nowTime.isBefore(storeBreak.getStartTime())
                                && nowTime.isBefore(storeBreak.getEndTime())
                );

        if (onBreak) {
            throw new ConflictException("Store is currently on a break");
        }

        if (!deliverySettings.isDeliveryEnabled()) {
            throw new ConflictException("Delivery is disabled for this store");
        }

        DeliveryType configuredType = deliverySettings.getDeliveryType();
        DeliveryType requestedType = request.deliveryMethod();

        if (configuredType != DeliveryType.BOTH && configuredType != requestedType) {
            throw new ConflictException(
                    "Selected delivery method is not supported by this store"
            );
        }

        if (!deliveryArea.getDeliverySettings().getStore().getId().equals(store.getId())) {
            throw new ConflictException("Delivery area does not belong to this store");
        }
        if (!deliveryArea.isActive()) {
            throw new ConflictException("Delivery area is not active");
        }

        String requestCity = request.deliveryCity() != null ? request.deliveryCity().trim() : "";
        String requestArea = request.deliveryAreaName() != null ? request.deliveryAreaName().trim() : "";
        
        if (!deliveryArea.getCity().equalsIgnoreCase(requestCity)) {
            throw new ConflictException(
                    "Delivery address city does not match the selected delivery area"
            );
        }

        if (!deliveryArea.getAreaName().equalsIgnoreCase(requestArea)) {
            throw new ConflictException(
                    "Delivery address area does not match the selected delivery area"
            );
        }
        List<StoreDeliveryRestriction> restrictions = storeDeliveryRestrictionRepository
                .findAllByDeliverySettings(deliverySettings)
                .stream()
                .filter(StoreDeliveryRestriction::isActive)
                .toList();
        
        for (StoreDeliveryRestriction restriction : restrictions) {

            String type = restriction.getRestrictionType().trim().toLowerCase();
            String value = restriction.getRestrictionValue();

            if ("block_city".equals(type) && value != null && value.equalsIgnoreCase(requestCity)) {
                throw new ConflictException("Delivery is restricted for this city");
            }

            if ("block_area".equals(type) && value != null && value.equalsIgnoreCase(requestArea)) {
                throw new ConflictException("Delivery is restricted for this area");
            }

            if ("block_delivery_method".equals(type)
                    && value != null
                    && value.equalsIgnoreCase(request.deliveryMethod().name())) {
                throw new ConflictException(
                        "Selected delivery method is restricted"
                );
            }
        }
        Order order = new Order();

        order.setStore(store);
        order.setCustomer(customer);

        String fullName = request.firstName() + (request.lastName() != null && !request.lastName().isBlank() ? " " + request.lastName() : "");
        order.setCustomerName(fullName.trim());
        order.setCustomerPhone(request.phone());

        order.setType(request.type());
        order.setCustomerNote(request.customerNote());

        order.setRecipientName(request.recipientName());
        order.setRecipientPhone(request.recipientPhone());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setDeliveryInstructions(request.deliveryInstructions());
        order.setDeliveryMethod(request.deliveryMethod().name());
        order.setDeliveryAreaName(deliveryArea.getAreaName());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequestDto itemRequest : request.items()) {

            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (!product.getStore().getId().equals(store.getId())) {
                throw new ConflictException("Product does not belong to this store");
            }
            if (product.isPauseOrdering()) {
                throw new ConflictException("Ordering is paused for this product");
            }
            if (itemRequest.quantity() < product.getMinQuantity()) {
                throw new ConflictException(
                        "Quantity is less than the minimum allowed"
                );
            }

            if (product.getMaxQuantity() != null
                    && itemRequest.quantity() > product.getMaxQuantity()) {
                throw new ConflictException(
                        "Quantity exceeds the maximum allowed"
                );
            }

            if (itemRequest.quantity() > product.getStock()) {
                throw new ConflictException(
                        "Not enough product in stock"
                );
            }
            product.setStock(product.getStock() - itemRequest.quantity());
            
            OrderItem item = new OrderItem();

            item.setOrder(order);
            item.setProduct(product);

            item.setProductNameSnapshot(product.getName());
            item.setProductPriceSnapshot(product.getPrice());

            item.setQuantity(itemRequest.quantity());

            BigDecimal lineTotal =
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.quantity()));
            Map<UUID, Integer> selectedByGroup = new HashMap<>();
            if (itemRequest.modifiers() != null) {
                for (OrderItemModifierRequestDto modifierRequest : itemRequest.modifiers()) {

                    ModifierOption modifierOption = modifierOptionRepository.findById(modifierRequest.modifierOptionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Modifier option not found"));

                    if (!modifierOption.getGroup().getStore().getId().equals(store.getId())) {
                        throw new ConflictException("Modifier option does not belong to this store");

                    }
                    boolean groupLinkedToProduct = product.getModifierGroups().stream()
                            .anyMatch(group ->
                                    group.getId().equals(modifierOption.getGroup().getId())
                            );

                    if (!groupLinkedToProduct) {
                        throw new ConflictException(
                                "Modifier option is not available for this product"
                        );
                    }
                    UUID groupId = modifierOption.getGroup().getId();
                    selectedByGroup.merge(groupId, 1, Integer::sum);
                    OrderItemModifier modifier = new OrderItemModifier();

                    modifier.setOrderItem(item);
                    modifier.setModifierOption(modifierOption);
                    modifier.setModifierNameSnapshot(modifierOption.getName());
                    modifier.setModifierPriceSnapshot(modifierOption.getPrice());

                    item.addModifier(modifier);

                    lineTotal = lineTotal.add(
                            modifierOption.getPrice()
                                    .multiply(BigDecimal.valueOf(itemRequest.quantity()))
                    );
                }
            }
            for (var group : product.getModifierGroups()) {

                int selectedCount = selectedByGroup.getOrDefault(group.getId(), 0);

                int minimumRequired = group.isRequired()
                        ? Math.max(group.getMinSelect(), 1)
                        : group.getMinSelect();

                if (selectedCount < minimumRequired) {
                    throw new ConflictException(
                            "Not enough modifier options selected for group: " + group.getName()
                    );
                }

                if (group.getMaxSelect() != null
                        && selectedCount > group.getMaxSelect()) {
                    throw new ConflictException(
                            "Too many modifier options selected for group: " + group.getName()
                    );
                }
            }
            item.setLineTotal(lineTotal);

            order.addItem(item);

            subtotal = subtotal.add(lineTotal);
        }
        if (deliverySettings.getMinimumOrderAmount() != null
            && subtotal.compareTo(deliverySettings.getMinimumOrderAmount()) < 0) {
            throw new ConflictException("Minimum order amount is not reached");
        }
        for (StoreDeliveryRestriction restriction : restrictions) {

            String type = restriction.getRestrictionType().trim().toLowerCase();
            String value = restriction.getRestrictionValue();

            if ("min_order".equals(type) && value != null) {
                BigDecimal restrictedMinimum;

                try {
                    restrictedMinimum = new BigDecimal(value.trim());
                } catch (NumberFormatException e) {
                    throw new ConflictException(
                            "Invalid minimum order restriction value"
                    );
                }

                if (subtotal.compareTo(restrictedMinimum) < 0) {
                    throw new ConflictException(
                            "Restricted minimum order amount is not reached"
                    );
                }
            }
        }

    order.setDeliveryFee(deliveryArea.getDeliveryFee());
    order.setSubtotal(subtotal);
    order.setDiscountTotal(BigDecimal.ZERO);

    BigDecimal total = orderTotalCalculator.calculate(
            subtotal,
            order.getDeliveryFee(),
            order.getDiscountTotal()
    );
        order.setTotal(total);

        order.setOrderNumber(
                "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );

    Order saved = orderRepository.save(order);
    return toResponse(saved);
    }

    private Customer createNewCustomer(OrderRequestDto request) {
        Customer customer = new Customer();
        String fullName = request.firstName();
        if (request.lastName() != null && !request.lastName().isBlank()) {
            fullName += " " + request.lastName();
        }
        customer.setName(fullName.trim());
        customer.setPhone(request.phone());

        if (request.address() != null && !request.address().isBlank()) {
            CustomerAddress customerAddress = new CustomerAddress();
            customerAddress.setAddressLine(request.address());
            customerAddress.setDefault(true);

            customer.addAddress(customerAddress);
        }
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto trackOrder(String orderNumber, String phone) {
        Order order = orderRepository.findByOrderNumberAndCustomerPhone(orderNumber, phone)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toResponse(order);
    }

    @Transactional
    public OrderResponseDto acceptOrder(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!storeAccessService.hasStoreAccess(
                order.getStore().getSlug(),
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }
        if (order.getStatus() != OrderStatus.NEW) {
            throw new ConflictException("Only NEW orders can be accepted");
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order.setAcceptedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }
    @Transactional
    public OrderResponseDto rejectOrder(UUID orderId, String reason) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!storeAccessService.hasStoreAccess(
                order.getStore().getSlug(),
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }
        if (order.getStatus() != OrderStatus.NEW) {
            throw new ConflictException("Only NEW orders can be rejected");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }

        order.setStatus(OrderStatus.REJECTED);
        order.setRejectedAt(LocalDateTime.now());
        order.setRejectReason(reason);

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    @Transactional
    public OrderResponseDto cancelOrder(UUID orderId, String reason) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED ||
            order.getStatus() == OrderStatus.CANCELED  ||
            order.getStatus() == OrderStatus.REJECTED) {
            throw new ConflictException("Order cannot be cancelled in status: " + order.getStatus());
        }
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
            }
        }

        order.setStatus(OrderStatus.CANCELED);
        order.setCancelledAt(LocalDateTime.now());
        if (reason != null && !reason.isBlank()) {
            order.setCancelReason(reason);
        }

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }
    @Transactional
    public OrderResponseDto startOrder(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!storeAccessService.hasStoreAccess(
                order.getStore().getSlug(),
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }

        if (order.getStatus() != OrderStatus.ACCEPTED) {
            throw new ConflictException(
                    "Only ACCEPTED orders can be moved to IN_PROGRESS"
            );
        }

        order.setStatus(OrderStatus.IN_PROGRESS);

        return toResponse(orderRepository.save(order));
    }
    @Transactional
    public OrderResponseDto completeOrder(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!storeAccessService.hasStoreAccess(
                order.getStore().getSlug(),
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new ConflictException(
                    "Only IN_PROGRESS orders can be completed"
            );
        }

        order.setStatus(OrderStatus.COMPLETED);

        return toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getNewOrders(UUID storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        if (!storeAccessService.hasStoreAccess(store.getSlug(), "ROLE_MERCHANT")) {
            throw new SecurityException("No access to this store");
        }
        return orderRepository.findByStoreIdAndStatus(storeId, OrderStatus.NEW, Pageable.unpaged())
            .getContent()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if(!storeAccessService.hasStoreAccess(order.getStore().getSlug(), "ROLE_MERCHANT")) {
            throw new SecurityException("No access to this store");
        }
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getStoreOrders(UUID storeId, OrderStatus status, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        if (!storeAccessService.hasStoreAccess(store.getSlug(), "ROLE_MERCHANT")) {
            throw new SecurityException("No access to this store");
        }

        Page<Order> orders;
        if(status != null) {
            orders = orderRepository.findByStoreIdAndStatus(storeId, status, pageable);
        } else {
            orders = orderRepository.findByStoreId(storeId, pageable);
        }

        return orders.map(this::toResponse);
    }

    private OrderResponseDto toResponse(Order order) {

        List<OrderItemResponseDto> items = order.getItems()
                .stream()
                .map(item -> {

                    List<OrderItemModifierResponseDto> modifiers =
                            item.getModifiers()
                                    .stream()
                                    .map(modifier ->
                                            new OrderItemModifierResponseDto(
                                                    modifier.getId(),
                                                    modifier.getModifierOption().getId(),
                                                    modifier.getModifierNameSnapshot(),
                                                    modifier.getModifierPriceSnapshot()
                                            )
                                    )
                                    .toList();

                    return new OrderItemResponseDto(
                            item.getId(),
                            item.getProduct().getId(),
                            item.getProductNameSnapshot(),
                            item.getProductPriceSnapshot(),
                            item.getQuantity(),
                            item.getLineTotal(),
                            modifiers
                    );
                })
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getStore().getId(),
                order.getCustomer() != null ? order.getCustomer().getId() : null,
                order.getOrderNumber(),
                order.getType(),
                order.getStatus(),
                order.getCustomerNote(),
                order.getRecipientName(),
                order.getRecipientPhone(),
                order.getDeliveryAddress(),
                order.getDeliveryInstructions(),
                order.getDeliveryAreaName(),
                order.getDeliveryMethod(),
                order.getSubtotal(),
                order.getDeliveryFee(),
                order.getDiscountTotal(),
                order.getTotal(),
                order.getCurrency(),
                items
        );
    }
}
