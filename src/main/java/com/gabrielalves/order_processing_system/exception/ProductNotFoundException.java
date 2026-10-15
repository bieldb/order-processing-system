package com.gabrielalves.order_processing_system.exception;

import java.util.UUID;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(UUID id) {
        super("Produto não encontrado: " + id);
    }
}
