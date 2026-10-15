package com.gabrielalves.order_processing_system.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductRequestDTO(

    @NotBlank
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    String name,

    @NotNull
    @PositiveOrZero(message = "Preço não pode ser negativo")
    @Digits(integer = 17, fraction = 2, message = "Preço deve ter no máximo 2 casas decimais")
    BigDecimal price,

    @NotNull
    @PositiveOrZero(message = "Estoque não pode ser negativo")
    Integer stock) {

}
