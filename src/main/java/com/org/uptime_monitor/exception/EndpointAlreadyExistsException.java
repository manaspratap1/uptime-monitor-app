package com.org.uptime_monitor.exception;

public class EndpointAlreadyExistsException extends RuntimeException{
    public EndpointAlreadyExistsException(String message) {
        super(message);
    }
}
