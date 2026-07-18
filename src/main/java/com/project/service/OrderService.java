package com.project.service;

import com.project.dto.OrderRequestDto;
import com.project.dto.OrderResponseDto;
import com.project.entity.Customer;
import com.project.entity.Order;
import com.project.entity.Store;
import com.project.repository.CustomerRepository;
import com.project.repository.OrderRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.project.dto.OrderItemRequestDto;
import com.project.entity.OrderItem;
import com.project.entity.Product;
import com.project.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.UUID;
import com.project.dto.OrderItemResponseDto;
import java.util.List;
import com.project.dto.OrderItemModifierRequestDto;
import com.project.dto.OrderItemModifierResponseDto;
import com.project.entity.ModifierOption;
import com.project.entity.OrderItemModifier;
import com.project.repository.ModifierOptionRepository;
import com.project.entity.OrderStatus;
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

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));


        Order order = new Order();

        order.setStore(store);
        order.setCustomer(customer);
        order.setType(request.type());
        order.setCustomerNote(request.customerNote());

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

        order.setSubtotal(subtotal);

        BigDecimal total = orderTotalCalculator.calculate(
                subtotal,
                order.getDeliveryFee(),
                order.getDiscountTotal()
        );

        order.setTotal(total);

        order.setOrderNumber(
                "ORD-" + UUID.randomUUID()
        );


        Order saved = orderRepository.save(order);

        return toResponse(saved);
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
        order.setStatus(OrderStatus.REJECTED);
        order.setRejectedAt(LocalDateTime.now());
        order.setRejectReason(reason);

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
        return orderRepository.findAllByStoreAndStatus(store, OrderStatus.NEW)
            .stream()
            .map(this::toResponse)
            .toList();
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
                order.getCustomer().getId(),
                order.getOrderNumber(),
                order.getType(),
                order.getStatus(),
                order.getCustomerNote(),
                order.getSubtotal(),
                order.getDeliveryFee(),
                order.getDiscountTotal(),
                order.getTotal(),
                order.getCurrency(),
                items
        );
    }
}
