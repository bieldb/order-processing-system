package com.gabrielalves.order_processing_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.gabrielalves.order_processing_system.dto.OrderItemResponseDTO;
import com.gabrielalves.order_processing_system.dto.OrderResponseDTO;
import com.gabrielalves.order_processing_system.entity.OrderStatus;
import com.gabrielalves.order_processing_system.exception.CustomerNotFoundException;
import com.gabrielalves.order_processing_system.exception.InsufficientStockException;
import com.gabrielalves.order_processing_system.exception.InvalidOrderStatusException;
import com.gabrielalves.order_processing_system.exception.OrderNotFoundException;
import com.gabrielalves.order_processing_system.service.OrderService;

@WebMvcTest({ OrderController.class, CustomerOrderController.class })
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private final UUID id = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    private final String validBody = """
            {"customerId": "%s", "items": [{"productId": "%s", "quantity": 2}]}
            """.formatted(customerId, productId);

    @Test
    void shouldCreateOrderAndReturnLocation() throws Exception {
        when(orderService.create(any())).thenReturn(response(OrderStatus.CREATED));

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(validBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/orders/" + id))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(399.80))
                .andExpect(jsonPath("$.items[0].unitPrice").value(199.90));
    }

    @Test
    void shouldReturn400WhenOrderHasNoItems() throws Exception {
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\": \"%s\", \"items\": []}".formatted(customerId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("items"));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldReturn400WhenItemQuantityIsNotPositive() throws Exception {
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\": \"%s\", \"items\": [{\"productId\": \"%s\", \"quantity\": 0}]}"
                                .formatted(customerId, productId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("items[0].quantity"));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
        when(orderService.create(any())).thenThrow(new CustomerNotFoundException(customerId));

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(validBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenStockIsInsufficient() throws Exception {
        when(orderService.create(any())).thenThrow(new InsufficientStockException(productId, 2, 1));

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(validBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Estoque insuficiente para o produto %s: solicitado 2, disponível 1".formatted(productId)));
    }

    @Test
    void shouldFindOrderById() throws Exception {
        when(orderService.findById(id)).thenReturn(response(OrderStatus.CREATED));

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }

    @Test
    void shouldReturn404WhenOrderDoesNotExist() throws Exception {
        when(orderService.findById(id)).thenThrow(new OrderNotFoundException(id));

        mockMvc.perform(get("/orders/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/orders/" + id));
    }

    @Test
    void shouldListOrdersFilteredByStatusNewestFirst() throws Exception {
        Pageable expected = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        when(orderService.findAll(OrderStatus.PROCESSING, expected))
                .thenReturn(new PageImpl<>(List.of(response(OrderStatus.PROCESSING)), expected, 1));

        mockMvc.perform(get("/orders").param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PROCESSING"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void shouldReturn400WhenStatusFilterIsInvalid() throws Exception {
        mockMvc.perform(get("/orders").param("status", "SHIPPED"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldListOrdersOfCustomer() throws Exception {
        when(orderService.findByCustomer(eq(customerId), any()))
                .thenReturn(new PageImpl<>(List.of(response(OrderStatus.CREATED))));

        mockMvc.perform(get("/customers/{id}/orders", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerId").value(customerId.toString()));
    }

    @Test
    void shouldProcessOrder() throws Exception {
        when(orderService.process(id)).thenReturn(response(OrderStatus.PROCESSING));

        mockMvc.perform(post("/orders/{id}/process", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void shouldCompleteOrder() throws Exception {
        when(orderService.complete(id)).thenReturn(response(OrderStatus.COMPLETED));

        mockMvc.perform(post("/orders/{id}/complete", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldCancelOrder() throws Exception {
        when(orderService.cancel(id)).thenReturn(response(OrderStatus.CANCELLED));

        mockMvc.perform(post("/orders/{id}/cancel", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldReturn409OnInvalidStatusTransition() throws Exception {
        when(orderService.cancel(id))
                .thenThrow(new InvalidOrderStatusException(OrderStatus.COMPLETED, OrderStatus.CANCELLED));

        mockMvc.perform(post("/orders/{id}/cancel", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Transição de status inválida: COMPLETED -> CANCELLED"));
    }

    private OrderResponseDTO response(OrderStatus status) {
        OrderItemResponseDTO item = new OrderItemResponseDTO(UUID.randomUUID(), productId, 2,
                new BigDecimal("199.90"), new BigDecimal("399.80"));
        return new OrderResponseDTO(id, customerId, status, LocalDateTime.of(2026, 9, 18, 10, 30),
                new BigDecimal("399.80"), List.of(item));
    }
}
