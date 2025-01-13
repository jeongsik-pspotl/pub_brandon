package com.pspotl.sidebranden.common.error;

public class WHiveUnauthorizedException extends WHiveBaseException {
    public WHiveUnauthorizedException(String message) {
        super(message, 401);
    }
}
