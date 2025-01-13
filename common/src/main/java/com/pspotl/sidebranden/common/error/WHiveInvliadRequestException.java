package com.pspotl.sidebranden.common.error;

public class WHiveInvliadRequestException extends WHiveBaseException {
    public WHiveInvliadRequestException(String message) {
        super(message, 400);
    }
}
