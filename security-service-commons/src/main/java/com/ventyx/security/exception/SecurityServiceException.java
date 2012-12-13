package com.ventyx.security.exception;

/**
 * API exception for handling internal business rule and validation errors
 */
public class SecurityServiceException extends Exception {

    public SecurityServiceException(String message) {
        super(message);
    }
}
