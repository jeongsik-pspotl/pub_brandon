package com.pspotl.sidebranden.common.error;

public class WHiveConflictException extends WHiveBaseException {
    public WHiveConflictException(String message) {
        super(message, 409);
    }
}
