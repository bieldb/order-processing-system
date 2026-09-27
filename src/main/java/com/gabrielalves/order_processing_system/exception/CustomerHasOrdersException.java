package com.gabrielalves.order_processing_system.exception;

import java.util.UUID;

public class CustomerHasOrdersException extends ConflictException {

    public CustomerHasOrdersException(UUID id) {
        super("Cliente possui pedidos e não pode ser excluído: " + id);
    }
}
