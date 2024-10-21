package com.flxpoint.customer.exception;

public class CrmCommunicationException extends RuntimeException {
    public CrmCommunicationException(String message) {
        super(message);
    }

    public CrmCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
