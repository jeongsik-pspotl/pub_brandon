package com.inswave.whive.common.error;

public class WHiveInvliadRequestException extends WHiveBaseException {
    public WHiveInvliadRequestException(String message) {
        super(message, 400);
    }
}
