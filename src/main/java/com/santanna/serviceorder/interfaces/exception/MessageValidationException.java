package com.santanna.serviceorder.interfaces.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MessageValidationException extends RuntimeException {
    public MessageValidationException(String message) {
        super(message);
    }


}