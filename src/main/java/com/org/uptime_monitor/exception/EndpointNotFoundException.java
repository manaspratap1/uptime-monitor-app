package com.org.uptime_monitor.exception;

public class EndpointNotFoundException extends RuntimeException {
    public EndpointNotFoundException(String message) {
        super(message);
    }
}
