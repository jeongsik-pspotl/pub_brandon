package com.pspotl.sidebranden.common.error;

public class WHiveNotFoundException extends WHiveBaseException {
    public WHiveNotFoundException(String message) {
        super(message, 404);
    }
}
