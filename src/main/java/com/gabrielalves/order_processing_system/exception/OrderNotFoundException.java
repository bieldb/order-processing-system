package com.gabrielalves.order_processing_system.exception;

import java.util.UUID;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(UUID id) {
        super("Pedido não encontrado: " + id);
    }
}
