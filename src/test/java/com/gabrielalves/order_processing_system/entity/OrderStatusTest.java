package com.gabrielalves.order_processing_system.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OrderStatusTest {

    // Tabela de transições da seção 4 do desafio.
    @ParameterizedTest(name = "{0} -> {1}: {2}")
    @CsvSource({
            "CREATED,    PROCESSING, true",
            "CREATED,    CANCELLED,  true",
            "PROCESSING, COMPLETED,  true",
            "PROCESSING, CANCELLED,  true",
            "COMPLETED,  CANCELLED,  false",
            "COMPLETED,  PROCESSING, false",
            "CANCELLED,  PROCESSING, false",
            "CANCELLED,  COMPLETED,  false",
            "CREATED,    COMPLETED,  false",
            "PROCESSING, CREATED,    false",
            "PROCESSING, PROCESSING, false"
    })
    void shouldRespectAllowedTransitions(OrderStatus current, OrderStatus next, boolean allowed) {
        assertThat(current.canTransitionTo(next)).isEqualTo(allowed);
    }
}
