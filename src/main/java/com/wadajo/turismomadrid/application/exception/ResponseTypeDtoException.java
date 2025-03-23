package com.wadajo.turismomadrid.application.exception;


public class ResponseTypeDtoException extends RuntimeException {
    public ResponseTypeDtoException(String message, RuntimeException e) {
        super(message, e);
    }

    public ResponseTypeDtoException(String message) {
        super(message);
    }
}
