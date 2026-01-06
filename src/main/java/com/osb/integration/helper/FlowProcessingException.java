package com.osb.integration.helper;

/**
 * Exception thrown when flow processing fails
 */
public class FlowProcessingException extends Exception {
    
    public FlowProcessingException(String message) {
        super(message);
    }
    
    public FlowProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
