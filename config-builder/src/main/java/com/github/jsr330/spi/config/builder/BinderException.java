package com.github.jsr330.spi.config.builder;

public class BinderException extends RuntimeException {
    
    private static final long serialVersionUID = 3546496886629517153L;
    
    public BinderException() {
    }
    
    public BinderException(String message) {
        super(message);
    }
    
    public BinderException(Throwable cause) {
        super(cause);
    }
    
    public BinderException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
