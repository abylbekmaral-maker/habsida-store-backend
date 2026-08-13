package com.project.controller;

import com.project.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.project.security.JwtTokenProvider;
import com.project.security.CustomUserDetailsService;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldAcceptOrder() throws Exception {

        UUID orderId = UUID.randomUUID();

        mockMvc.perform(
                patch("/api/orders/{orderId}/accept", orderId)
        ).andExpect(status().isOk());

        verify(orderService).acceptOrder(orderId);
    }
    @Test
    void shouldCreateOrder() throws Exception {

        mockMvc.perform(
                post("/api/orders")
                        .contentType("application/json")
                        .content("""
                        {
                        "storeId": "00000000-0000-0000-0000-000000000000",
                        "customerId": null,
                        "type": "DELIVERY",
                         "customerNote": "note",

                        "recipientName": "name",
                        "recipientPhone": "number",
                        "deliveryAddress": "address",
                        "deliveryCity": "city",
                        "deliveryAreaName": "area",
                        "deliveryInstructions": "instructions",
                        "deliveryAreaId": "00000000-0000-0000-0000-000000000001",
                        "deliveryMethod": "OWN_COURIER",

                        "items": [
                            {
                            "productId": "00000000-0000-0000-0000-000000000000",
                            "quantity": 1,
                            "modifiers": []
                            }
                        ],

                        "firstName": "name",
                        "lastName": "lastname",
                        "phone": "number",
                        "email": "email@gmail.com",
                        "address": "address"
                        }
                    """)
        ).andExpect(status().isCreated());

        verify(orderService).createOrder(any());
    }
    @Test
    void shouldReturnNewOrders() throws Exception {

        UUID storeId = UUID.randomUUID();

        when(orderService.getNewOrders(storeId))
                .thenReturn(List.of());

        mockMvc.perform(
                get("/api/orders/store/{storeId}/new", storeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));

        verify(orderService).getNewOrders(storeId);
    }
}
