package com.gabrielalves.order_processing_system.exception;

/**
 * Recurso solicitado não existe (HTTP 404).
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
