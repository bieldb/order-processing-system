package com.gabrielalves.order_processing_system.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponseDTO(UUID id, String name, BigDecimal price, Integer stock) {

}
