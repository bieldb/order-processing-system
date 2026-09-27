package com.gabrielalves.order_processing_system.exception.handler;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Corpo padrão das respostas de erro da API.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> fields) {

    public record FieldViolation(String field, String message) {
    }
}
