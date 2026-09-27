package com.gabrielalves.order_processing_system.exception;

import java.util.UUID;

public class CustomerNotFoundException extends NotFoundException {

    public CustomerNotFoundException(UUID id) {
        super("Cliente não encontrado: " + id);
    }
}
