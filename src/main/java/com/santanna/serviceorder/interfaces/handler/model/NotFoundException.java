package com.santanna.serviceorder.interfaces.handler.model;

public class NotFoundException extends RuntimeException {

        public NotFoundException(String message) {
            super(message);
        }
}
