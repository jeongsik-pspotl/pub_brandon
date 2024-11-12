package com.inswave.whive.headquater.security.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenValidationData {

    boolean isValid;
    Exception exception;

    public TokenValidationData(boolean isValid, Exception exception) {
        this.isValid = isValid;
        this.exception = exception;
    }

}
