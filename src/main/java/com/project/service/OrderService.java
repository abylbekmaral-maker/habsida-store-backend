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
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));


        Order order = new Order();

        order.setStore(store);
        order.setCustomer(customer);
        order.setType(request.type());
        order.setCustomerNote(request.customerNote());

        order.setDeliveryFee(request.deliveryFee() != null ? request.deliveryFee() : BigDecimal.ZERO);
        order.setDiscountTotal(request.discountTotal() != null ? request.discountTotal() : BigDecimal.ZERO);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequestDto itemRequest : request.items()) {

            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            OrderItem item = new OrderItem();

            item.setOrder(order);
            item.setProduct(product);

            item.setProductNameSnapshot(product.getName());
            item.setProductPriceSnapshot(product.getPrice());

            item.setQuantity(itemRequest.quantity());

            BigDecimal lineTotal =
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.quantity()));
            if (itemRequest.modifiers() != null) {
                for (OrderItemModifierRequestDto modifierRequest : itemRequest.modifiers()) {

                    ModifierOption modifierOption = modifierOptionRepository.findById(modifierRequest.modifierOptionId())
                            .orElseThrow(() -> new IllegalArgumentException("Modifier option not found"));

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
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!storeAccessService.hasStoreAccess(
                order.getStore().getSlug(),
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }
        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalArgumentException("Only NEW orders can be accepted");
        }

        order.setStatus(OrderStatus.ACCEPTED);
        order.setAcceptedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }
    @Transactional
    public OrderResponseDto rejectOrder(UUID orderId, String reason) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!storeAccessService.hasStoreAccess(
                order.getStore().getSlug(),
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }
        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalArgumentException("Only NEW orders can be rejected");
        }
        order.setStatus(OrderStatus.REJECTED);
        order.setRejectedAt(LocalDateTime.now());
        order.setRejectReason(reason);

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getNewOrders(UUID storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new IllegalArgumentException("Store not found"));

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
