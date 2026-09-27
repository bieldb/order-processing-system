package com.gabrielalves.order_processing_system.exception;

/**
 * Operação que viola uma regra de negócio ou o estado atual de um recurso (HTTP 409).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
