package com.project.service;

import com.project.entity.Customer;
import com.project.entity.Order;
import com.project.entity.OrderStatus;
import com.project.entity.Store;
import com.project.exception.ConflictException;
import com.project.repository.CustomerRepository;
import com.project.repository.ModifierOptionRepository;
import com.project.repository.OrderRepository;
import com.project.repository.ProductRepository;
import com.project.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModifierOptionRepository modifierOptionRepository;

    @Mock
    private StoreAccessService storeAccessService;

    @Mock
    private OrderTotalCalculator orderTotalCalculator;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldAcceptNewOrder() {

        UUID orderId = UUID.randomUUID();

        Store store = new Store();
        store.setSlug("test-store");

        Customer customer = new Customer();

        Order order = new Order();
        order.setStore(store);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.NEW);
        order.setSubtotal(BigDecimal.ZERO);
        order.setDeliveryFee(BigDecimal.ZERO);
        order.setDiscountTotal(BigDecimal.ZERO);
        order.setTotal(BigDecimal.ZERO);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(storeAccessService.hasStoreAccess(
                "test-store",
                "ROLE_MERCHANT"
        )).thenReturn(true);

        when(orderRepository.save(order))
                .thenReturn(order);

        orderService.acceptOrder(orderId);

        assertEquals(OrderStatus.ACCEPTED, order.getStatus());
        assertNotNull(order.getAcceptedAt());

        verify(orderRepository).save(order);
    }

    @Test
    void shouldNotAcceptOrderWhenStatusIsNotNew() {

        UUID orderId = UUID.randomUUID();

        Store store = new Store();
        store.setSlug("test-store");

        Customer customer = new Customer();

        Order order = new Order();
        order.setStore(store);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(storeAccessService.hasStoreAccess(
                "test-store",
                "ROLE_MERCHANT"
        )).thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> orderService.acceptOrder(orderId)
        );

        verify(orderRepository, never()).save(any());
    }
}