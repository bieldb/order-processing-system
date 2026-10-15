package com.gabrielalves.order_processing_system.exception;

import com.gabrielalves.order_processing_system.entity.OrderStatus;

public class InvalidOrderStatusException extends ConflictException {

    public InvalidOrderStatusException(OrderStatus current, OrderStatus target) {
        super("Transição de status inválida: %s -> %s".formatted(current, target));
    }
}
