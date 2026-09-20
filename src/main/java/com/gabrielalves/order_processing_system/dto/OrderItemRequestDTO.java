package com.gabrielalves.order_processing_system.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * OrderItemRequestDTO
 */
public record OrderItemRequestDTO(@NotNull UUID productId, @NotNull @Positive Integer quantity) {

}
