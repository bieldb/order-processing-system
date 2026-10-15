package com.gabrielalves.order_processing_system.exception;

import java.util.UUID;

public class ProductInUseException extends ConflictException {

    public ProductInUseException(UUID id) {
        super("Produto está presente em pedidos e não pode ser excluído: " + id);
    }
}
