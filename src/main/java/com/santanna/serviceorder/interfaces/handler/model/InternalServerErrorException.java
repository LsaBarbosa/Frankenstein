package com.santanna.serviceorder.interfaces.handler.model;

public class InternalServerErrorException extends RuntimeException {

        public InternalServerErrorException(String message) {
            super(message);
        }
}
