package com.gabrielalves.order_processing_system.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDTO(UUID id, UUID productId, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {

}
