package com.pspotl.sidebranden.common.error;

public class WHiveForbiddenException extends WHiveBaseException {
    public WHiveForbiddenException(String message) {
        super(message, 403);
    }
}