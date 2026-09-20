package com.gabrielalves.order_processing_system.exception;

import com.gabrielalves.order_processing_system.exception.businessexception.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {

    public EmailAlreadyExistsException(String email) {
        super("já existe um cadastro com esse email: " + email);
    }
}
