package com.trafegus.poc.web.exceptions;

public class MissingPermissionsException extends RuntimeException {
    public MissingPermissionsException(String message) {
        super(message);
    }
}
