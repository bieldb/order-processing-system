package com.gabrielalves.order_processing_system.dto;

import java.util.List;
import java.util.UUID;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDTO(@NotNull UUID customerId, @NotEmpty @Valid List<OrderItemRequestDTO> items) {
}
