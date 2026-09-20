package com.gabrielalves.order_processing_system.dto;

import java.util.UUID;

public record CustomerResponseDTO(UUID id, String name, String email) {

}
