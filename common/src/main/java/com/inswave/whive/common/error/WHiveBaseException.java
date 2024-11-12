package com.inswave.whive.common.error;

public class WHiveBaseException extends RuntimeException {
    public String message = "Internal Error";
    public int status = 500;
    
    public WHiveBaseException(String message, int status) {
        super(message);
        
        this.status = status;
    }
}
