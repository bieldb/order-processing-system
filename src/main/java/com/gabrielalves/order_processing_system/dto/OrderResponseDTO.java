package com.gabrielalves.order_processing_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.gabrielalves.order_processing_system.entity.OrderStatus;

public record OrderResponseDTO(UUID id, UUID customerId, OrderStatus status, LocalDateTime createdAt, BigDecimal total, List<OrderItemResponseDTO> items) {
}
