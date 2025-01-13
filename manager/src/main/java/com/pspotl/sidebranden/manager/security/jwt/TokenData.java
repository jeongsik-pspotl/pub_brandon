package com.pspotl.sidebranden.manager.security.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenData {

    String tokenType;
    String accessToken;
    String refreshToken;

    public TokenData(String accessToken, String refreshToken) {
        this.tokenType = "Bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
