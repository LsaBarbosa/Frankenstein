package com.santanna.serviceorder.interfaces.handler.model;

public class BadRequestException extends RuntimeException {

        public BadRequestException(String message) {
            super(message);
        }
}
