package com.santanna.serviceorder.application.usecase.exception;

import org.apache.coyote.BadRequestException;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}