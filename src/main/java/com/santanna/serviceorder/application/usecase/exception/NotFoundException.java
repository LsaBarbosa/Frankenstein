package com.santanna.serviceorder.application.usecase.exception;

public class NotFoundException extends RuntimeException {

        public NotFoundException(String message) {
            super(message);
        }
}
