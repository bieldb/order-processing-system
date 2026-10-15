package com.gabrielalves.order_processing_system.exception;

import java.util.UUID;

public class InsufficientStockException extends ConflictException {

    public InsufficientStockException(UUID productId, int requested, int available) {
        super("Estoque insuficiente para o produto %s: solicitado %d, disponível %d"
                .formatted(productId, requested, available));
    }
}
