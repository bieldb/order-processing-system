package com.gabrielalves.order_processing_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO(
    
    @NotBlank 
    @Size(min = 3, max = 80, message = "Nome deve ter entre 3 e 80 caracteres")
    String name, 
    
    @NotBlank 
    @Email
    String email) {

}
