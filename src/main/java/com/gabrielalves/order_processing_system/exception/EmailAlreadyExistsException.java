package com.gabrielalves.order_processing_system.exception;

public class EmailAlreadyExistsException extends ConflictException {

    public EmailAlreadyExistsException(String email) {
        super("Já existe um cadastro com esse email: " + email);
    }
}
